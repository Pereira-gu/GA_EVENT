package com.unicid.ga.event.presenca;

import com.unicid.ga.event.evento.Evento;
import com.unicid.ga.event.evento.EventoRepository;
import com.unicid.ga.event.user.Usuario;
import com.unicid.ga.event.user.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inscricoes")
public class InscricaoController {

    @Autowired
    private RegistroPresencaRepository registroPresencaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    /**
     * Inscreve um aluno em um evento (Status inicial: PENDENTE).
     */
    @PostMapping
    public ResponseEntity<?> inscreverAluno(@RequestBody InscricaoRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(request.getUsuarioId());
        Optional<Evento> eventoOpt = eventoRepository.findById(request.getEventoId());

        if (usuarioOpt.isEmpty() || eventoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário ou Evento não encontrado.");
        }

        // Verifica se já está inscrito
        Optional<RegistroPresenca> inscricaoExistente = registroPresencaRepository
                .findByUsuarioIdAndEventoId(request.getUsuarioId(), request.getEventoId());

        if (inscricaoExistente.isPresent()) {
            return ResponseEntity.badRequest().body("Aluno já inscrito neste evento.");
        }

        RegistroPresenca novaInscricao = new RegistroPresenca();
        novaInscricao.setUsuarioId(request.getUsuarioId());
        novaInscricao.setEventoId(request.getEventoId());
        novaInscricao.setEntrada(LocalDateTime.now()); // Data da inscrição
        novaInscricao.setStatus("PENDENTE"); // Inscrito, mas não validado pelo QR Code ainda

        registroPresencaRepository.save(novaInscricao);

        return ResponseEntity.ok("Inscrição realizada com sucesso!");
    }

    /**
     * Histórico de eventos de um aluno específico.
     */
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<HistoricoAlunoResponse>> listarHistoricoAluno(@PathVariable UUID alunoId) {
        List<RegistroPresenca> registros = registroPresencaRepository.findByUsuarioId(alunoId);

        List<HistoricoAlunoResponse> historico = registros.stream().map(registro -> {
            Evento evento = eventoRepository.findById(registro.getEventoId()).orElse(null);
            String tituloEvento = (evento != null) ? evento.getTitulo() : "Evento Desconhecido";
            LocalDateTime dataEvento = (evento != null) ? evento.getDataInicio() : null;

            return new HistoricoAlunoResponse(
                    registro.getEventoId(),
                    tituloEvento,
                    dataEvento,
                    registro.getStatus(),
                    registro.getEntrada(),
                    registro.getSaida()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(historico);
    }

    /**
     * Lista de alunos inscritos em um evento específico (Para o Organizador).
     */
    @GetMapping("/evento/{eventoId}/inscritos")
    public ResponseEntity<List<InscritosEventoResponse>> listarInscritosEvento(@PathVariable UUID eventoId) {
        List<RegistroPresenca> registros = registroPresencaRepository.findByEventoId(eventoId);

        List<InscritosEventoResponse> inscritos = registros.stream().map(registro -> {
            Usuario usuario = usuarioRepository.findById(registro.getUsuarioId()).orElse(null);
            String nomeUsuario = (usuario != null) ? usuario.getNome() : "Usuário Desconhecido";
            String emailUsuario = (usuario != null) ? usuario.getEmail() : "Sem E-mail";

            return new InscritosEventoResponse(
                    registro.getUsuarioId(),
                    nomeUsuario,
                    emailUsuario,
                    registro.getStatus()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(inscritos);
    }
}

// DTOs

class InscricaoRequest {
    private UUID usuarioId;
    private UUID eventoId;

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }

    public UUID getEventoId() { return eventoId; }
    public void setEventoId(UUID eventoId) { this.eventoId = eventoId; }
}

class HistoricoAlunoResponse {
    private UUID eventoId;
    private String tituloEvento;
    private LocalDateTime dataEvento;
    private String statusPresenca;
    private LocalDateTime entrada;
    private LocalDateTime saida;

    public HistoricoAlunoResponse(UUID eventoId, String tituloEvento, LocalDateTime dataEvento, String statusPresenca, LocalDateTime entrada, LocalDateTime saida) {
        this.eventoId = eventoId;
        this.tituloEvento = tituloEvento;
        this.dataEvento = dataEvento;
        this.statusPresenca = statusPresenca;
        this.entrada = entrada;
        this.saida = saida;
    }

    public UUID getEventoId() { return eventoId; }
    public String getTituloEvento() { return tituloEvento; }
    public LocalDateTime getDataEvento() { return dataEvento; }
    public String getStatusPresenca() { return statusPresenca; }
    public LocalDateTime getEntrada() { return entrada; }
    public LocalDateTime getSaida() { return saida; }
}

class InscritosEventoResponse {
    private UUID usuarioId;
    private String nomeUsuario;
    private String emailUsuario;
    private String statusPresenca;

    public InscritosEventoResponse(UUID usuarioId, String nomeUsuario, String emailUsuario, String statusPresenca) {
        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
        this.emailUsuario = emailUsuario;
        this.statusPresenca = statusPresenca;
    }

    public UUID getUsuarioId() { return usuarioId; }
    public String getNomeUsuario() { return nomeUsuario; }
    public String getEmailUsuario() { return emailUsuario; }
    public String getStatusPresenca() { return statusPresenca; }
}