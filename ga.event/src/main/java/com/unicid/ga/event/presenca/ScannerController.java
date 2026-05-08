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
import java.util.List;
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

        // 1. O aluno já se inscreveu antes (Status: PENDENTE)?
        Optional<RegistroPresenca> inscricaoPendente = registroPresencaRepository
                .findByUsuarioIdAndEventoIdAndStatus(usuarioId, evento.getId(), "PENDENTE");

        if (inscricaoPendente.isPresent()) {
            // O aluno estava inscrito, e este é o primeiro SCAN (Check-in real na porta)
            RegistroPresenca registro = inscricaoPendente.get();
            registro.setEntrada(LocalDateTime.now()); // Substitui a data de inscrição pela hora real de entrada
            registro.setStatus("ATIVO"); // Aluno está dentro do evento
            registroPresencaRepository.save(registro);
            return ResponseEntity.ok("Check-in realizado com sucesso! (Inscrição ativada)");
        }

        // 2. Alternância automática de status (Check-out)
        Optional<RegistroPresenca> registroAtivo = registroPresencaRepository
                .findByUsuarioIdAndEventoIdAndStatus(usuarioId, evento.getId(), "ATIVO");

        if (registroAtivo.isPresent()) {
            // Realiza o Check-out (Segundo Scan)
            RegistroPresenca registro = registroAtivo.get();
            registro.setSaida(LocalDateTime.now());

            // Calcula o tempo de permanência em minutos
            long minutosPermanencia = Duration.between(registro.getEntrada(), registro.getSaida()).toMinutes();
            
            // Regra: Deve ficar pelo menos 80% do tempo do evento
            double cargaHorariaMinima = evento.getCargaHoraria() * 0.8;

            if (minutosPermanencia >= cargaHorariaMinima) {
                registro.setStatus("CONCLUIDO");
            } else {
                registro.setStatus("INCOMPLETO"); // Saiu antes da hora
            }
            
            registroPresencaRepository.save(registro);

            // Lógica do Badge de Ouro: Se atingir 3 eventos CONCLUIDOS, ganha a badge
            verificarBadgeOuro(usuario);

            return ResponseEntity.ok("Check-out realizado! Tempo: " + minutosPermanencia + " minutos. Status final: " + registro.getStatus());
        }

        // Se chegou aqui e não caiu nem no PENDENTE nem no ATIVO, significa que o aluno tentou entrar
        // SEM ter se inscrito pelo app antes (ou a inscrição já foi concluída/cancelada).
        // Aqui você pode decidir se o porteiro pode forçar a entrada ou se recusa.
        // Vamos permitir a entrada direta ("comprou ingresso na hora"):
        RegistroPresenca novoRegistro = new RegistroPresenca();
        novoRegistro.setUsuarioId(usuarioId);
        novoRegistro.setEventoId(evento.getId());
        novoRegistro.setEntrada(LocalDateTime.now());
        novoRegistro.setStatus("ATIVO");
        registroPresencaRepository.save(novoRegistro);
        
        return ResponseEntity.ok("Check-in avulso realizado com sucesso! (Sem inscrição prévia)");
    }

    /**
     * Verifica o histórico do aluno e concede a Badge Ouro se ele atingir os critérios.
     */
    private void verificarBadgeOuro(Usuario usuario) {
        if (usuario.isBadgeOuro()) return; // Já tem a badge, não precisa recalcular

        List<RegistroPresenca> historico = registroPresencaRepository.findByUsuarioId(usuario.getId());
        
        long eventosConcluidos = historico.stream()
                .filter(reg -> "CONCLUIDO".equals(reg.getStatus()))
                .count();

        // Regra do negócio: 3 eventos concluídos = Badge Ouro
        if (eventosConcluidos >= 3) {
            usuario.setBadgeOuro(true);
            usuarioRepository.save(usuario);
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