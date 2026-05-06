package com.unicid.ga.event.presenca;

import com.unicid.ga.event.evento.Evento;
import com.unicid.ga.event.evento.EventoRepository;
import com.unicid.ga.event.user.Usuario;
import com.unicid.ga.event.user.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller responsável pela leitura e validação do QR Code e fluxo do porteiro.
 */
@RestController
@RequestMapping("/scanner")
public class ScannerController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private RegistroPresencaRepository registroPresencaRepository;

    /**
     * Valida o QR Code lido pelo porteiro e realiza o Check-in ou Check-out.
     */
    @PostMapping("/validar")
    public ResponseEntity<?> validarAcesso(@RequestBody ScannerRequest request) {
        // O QR Code contém a string formatada como "UUID;Timestamp"
        String[] partes = request.getQrCodeData().split(";");
        if (partes.length < 2) {
            return ResponseEntity.badRequest().body("QR Code inválido.");
        }

        UUID usuarioId;
        try {
            usuarioId = UUID.fromString(partes[0]);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("UUID do usuário inválido.");
        }

        long timestampGeracao = Long.parseLong(partes[1]);

        // Validação da segurança do tempo (5 minutos = 300 segundos)
        long timestampAtual = System.currentTimeMillis() / 1000L;
        if (Math.abs(timestampAtual - timestampGeracao) > 300) {
            return ResponseEntity.status(401).body("QR Code expirado.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        Optional<Evento> eventoOpt = eventoRepository.findById(request.getEventoId());

        if (usuarioOpt.isEmpty() || eventoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário ou evento não encontrados.");
        }

        Usuario usuario = usuarioOpt.get();
        Evento evento = eventoOpt.get();

        // Alternância automática de status (Check-in / Check-out)
        Optional<RegistroPresenca> registroAtivo = registroPresencaRepository
                .findByUsuarioIdAndEventoIdAndStatus(usuarioId, evento.getId(), "ATIVO");

        if (registroAtivo.isPresent()) {
            // Realiza o Check-out
            RegistroPresenca registro = registroAtivo.get();
            registro.setSaida(LocalDateTime.now());
            registro.setStatus("CONCLUIDO");

            long minutosPermanencia = Duration.between(registro.getEntrada(), registro.getSaida()).toMinutes();
            double cargaHorariaMinima = evento.getCargaHoraria() * 0.8;

            if (minutosPermanencia >= cargaHorariaMinima) {
                usuario.setBadgeOuro(true);
                usuarioRepository.save(usuario);
            }

            registroPresencaRepository.save(registro);
            return ResponseEntity.ok("Check-out realizado com sucesso!");
        } else {
            // Realiza o Check-in
            RegistroPresenca novoRegistro = new RegistroPresenca();
            novoRegistro.setUsuarioId(usuarioId);
            novoRegistro.setEventoId(evento.getId());
            novoRegistro.setEntrada(LocalDateTime.now());
            novoRegistro.setStatus("ATIVO");

            registroPresencaRepository.save(novoRegistro);
            return ResponseEntity.ok("Check-in realizado com sucesso!");
        }
    }
}

/**
 * DTO para a requisição de leitura do QR Code.
 */
class ScannerRequest {
    private String qrCodeData;
    private UUID eventoId;

    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }

    public UUID getEventoId() { return eventoId; }
    public void setEventoId(UUID eventoId) { this.eventoId = eventoId; }
}