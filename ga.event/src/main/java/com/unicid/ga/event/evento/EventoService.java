package com.unicid.ga.event.evento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Service do CRUD EVENTO
@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;
// C -> Criar
    public Evento criarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }
// R -> Read
    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }
// R -> Read by id
    public Optional<Evento> buscarPorId(UUID id) {
        return eventoRepository.findById(id);
    }
// U -> Update o evento
    public Evento atualizarEvento(UUID id, Evento eventoAtualizado) {
        return eventoRepository.findById(id).map(evento -> {
            evento.setTitulo(eventoAtualizado.getTitulo());
            evento.setLocal(eventoAtualizado.getLocal());
            evento.setDataInicio(eventoAtualizado.getDataInicio());
            evento.setCargaHoraria(eventoAtualizado.getCargaHoraria());
            return eventoRepository.save(evento);
        }).orElseThrow(() -> new RuntimeException("Evento não encontrado com o ID: " + id));
    }
//  D -> Delete evento
    public void deletarEvento(UUID id) {
        if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Evento não encontrado com o ID: " + id);
        }
    }
}