package com.unicid.ga.event.evento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

//Interface acesso ao DB para a entidade Evento
@Repository
public interface EventoRepository extends JpaRepository<Evento, UUID> {
}