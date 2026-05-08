package com.unicid.ga.event.presenca;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Repository de precensa :)
@Repository
public interface RegistroPresencaRepository extends JpaRepository<RegistroPresenca, Long> {
    Optional<RegistroPresenca> findByUsuarioIdAndEventoIdAndStatus(UUID usuarioId, UUID eventoId, String status);
    
    // Novo método para buscar o histórico de eventos de um aluno específico
    List<RegistroPresenca> findByUsuarioId(UUID usuarioId);
}