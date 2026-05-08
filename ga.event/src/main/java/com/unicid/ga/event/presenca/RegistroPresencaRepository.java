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
    
    // Buscar histórico do aluno
    List<RegistroPresenca> findByUsuarioId(UUID usuarioId);
    
    // Buscar todos os inscritos em um evento específico
    List<RegistroPresenca> findByEventoId(UUID eventoId);
    
    // Verificar se o aluno já está inscrito no evento, independente do status
    Optional<RegistroPresenca> findByUsuarioIdAndEventoId(UUID usuarioId, UUID eventoId);
}