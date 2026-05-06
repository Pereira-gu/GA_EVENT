package com.unicid.ga.event.evento;

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
 * Controller e Serviço unificados para gerenciar o registro de presença e o cálculo de permanência.
 */
@RestController
@RequestMapping("/api/presenca")
public class PresencaController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    /**
     * Registra o check-out do usuário, calcula o tempo de permanência e atualiza o badge de ouro se necessário.
     * * @param request Dados contendo o ID do usuário, do evento e o horário de entrada.
     * @return ResponseEntity com o resultado da operação.
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> registrarSaida(@RequestBody CheckOutRequest request) {
        // Busca as entidades no banco de dados (Supabase)
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(request.getUsuarioId());
        Optional<Evento> eventoOpt = eventoRepository.findById(request.getEventoId());

        if (usuarioOpt.isEmpty() || eventoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário ou evento não encontrados.");
        }

        Usuario usuario = usuarioOpt.get();
        Evento evento = eventoOpt.get();

        // Calcula o tempo de permanência em minutos
        LocalDateTime entrada = request.getDataEntrada();
        LocalDateTime saida = LocalDateTime.now();
        long minutosPermanencia = Duration.between(entrada, saida).toMinutes();

        // Executa a lógica de negócio do "Aluno de Ouro" (80% da carga horária)
        double cargaHorariaMinima = evento.getCargaHoraria() * 0.8;

        if (minutosPermanencia >= cargaHorariaMinima) {
            usuario.setBadgeOuro(true);
            usuarioRepository.save(usuario); // Atualiza o badge no banco de dados
        }

        return ResponseEntity.ok("Check-out registrado com sucesso. Permanência calculada.");
    }
}

/**
 * DTO para encapsular os dados da requisição de Check-out.
 */
class CheckOutRequest {
    private UUID usuarioId;
    private UUID eventoId;
    private LocalDateTime dataEntrada;

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }

    public UUID getEventoId() { return eventoId; }
    public void setEventoId(UUID eventoId) { this.eventoId = eventoId; }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }
}