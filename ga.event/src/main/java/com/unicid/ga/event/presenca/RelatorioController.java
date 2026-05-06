package com.unicid.ga.event.presenca;

import com.unicid.ga.event.evento.EventoRepository;
import com.unicid.ga.event.user.Usuario;
import com.unicid.ga.event.user.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller responsável por gerar relatórios e estatísticas para o Organizador.
 */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private RegistroPresencaRepository registroPresencaRepository;

    /**
     * Retorna a lista de candidatos que atingiram a meta de permanência (candidatos ao badge de ouro).
     * @param eventoId ID do evento a ser consultado.
     */
    @GetMapping("/candidatos-badge/{eventoId}")
    public ResponseEntity<?> obterCandidatosBadgeOuro(@PathVariable UUID eventoId) {
        // Busca todos os registros do evento
        List<RegistroPresenca> registros = registroPresencaRepository.findAll()
                .stream()
                .filter(r -> r.getEventoId().equals(eventoId))
                .collect(Collectors.toList());

        // Retorna apenas os usuários que possuem badge_ouro = true ou que cumpriram o requisito
        List<Usuario> candidatos = usuarioRepository.findAll()
                .stream()
                .filter(Usuario::isBadgeOuro)
                .collect(Collectors.toList());

        return ResponseEntity.ok(candidatos);
    }

    /**
     * Estatísticas gerais de presença do evento.
     * @param eventoId ID do evento.
     */
    @GetMapping("/estatisticas/{eventoId}")
    public ResponseEntity<?> obterEstatisticasEvento(@PathVariable UUID eventoId) {
        List<RegistroPresenca> registros = registroPresencaRepository.findAll()
                .stream()
                .filter(r -> r.getEventoId().equals(eventoId))
                .collect(Collectors.toList());

        long presentesAtuais = registros.stream().filter(r -> "ATIVO".equals(r.getStatus())).count();
        long totalParticipantes = registros.size();

        EstatisticasDTO estatisticas = new EstatisticasDTO(presentesAtuais, totalParticipantes);
        return ResponseEntity.ok(estatisticas);
    }
}

/**
 * DTO para encapsular as estatísticas do evento.
 */
class EstatisticasDTO {
    private long presentesAgora;
    private long totalParticipantes;

    public EstatisticasDTO(long presentesAgora, long totalParticipantes) {
        this.presentesAgora = presentesAgora;
        this.totalParticipantes = totalParticipantes;
    }

    public long getPresentesAgora() { return presentesAgora; }
    public void setPresentesAgora(long presentesAgora) { this.presentesAgora = presentesAgora; }

    public long getTotalParticipantes() { return totalParticipantes; }
    public void setTotalParticipantes(long totalParticipantes) { this.totalParticipantes = totalParticipantes; }
}