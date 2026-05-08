package com.unicid.ga.event.evento;

import com.unicid.ga.event.evento.dto.EventoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pela gestão de eventos (CRUD) para o perfil Organizador.
 */
@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    /**
     * Endpoint para criar um novo evento.
     * * @param request Dados do evento a ser criado.
     * @return ResponseEntity com o evento criado e o status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Evento> criarEvento(@RequestBody EventoDTO request) {
        Evento novoEvento = new Evento(
                request.getTitulo(),
                request.getLocal(),
                request.getDataInicio(),
                request.getCargaHoraria()
        );
        Evento eventoSalvo = eventoService.criarEvento(novoEvento);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoSalvo);
    }

    /**
     * Endpoint para listar todos os eventos cadastrados.
     * * @return Lista de eventos e status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Evento>> listarEventos() {
        List<Evento> eventos = eventoService.listarEventos();
        return ResponseEntity.ok(eventos);
    }

    /**
     * Endpoint para buscar um evento específico pelo seu UUID.
     * * @param id Identificador único do evento.
     * @return ResponseEntity com o evento encontrado ou status 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarPorId(@PathVariable UUID id) {
        return eventoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para atualizar os dados de um evento existente.
     * * @param id      Identificador único do evento a ser atualizado.
     * @param request Novos dados do evento.
     * @return ResponseEntity com o evento atualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Evento> atualizarEvento(@PathVariable UUID id, @RequestBody EventoDTO request) {
        Evento eventoAtualizado = new Evento(
                request.getTitulo(),
                request.getLocal(),
                request.getDataInicio(),
                request.getCargaHoraria()
        );
        Evento evento = eventoService.atualizarEvento(id, eventoAtualizado);
        return ResponseEntity.ok(evento);
    }

    /**
     * Endpoint para deletar um evento do sistema.
     * * @param id Identificador único do evento.
     * @return ResponseEntity sem conteúdo e status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEvento(@PathVariable UUID id) {
        eventoService.deletarEvento(id);
        return ResponseEntity.noContent().build();
    }
}