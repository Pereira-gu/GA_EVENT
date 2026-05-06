package com.unicid.ga.event.presenca;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

//model da entidade registro_precensa no db. vao ser essas variaveis que alterarao o db :)
@Entity
@Table(name = "registros_presenca")
public class RegistroPresenca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "evento_id", nullable = false)
    private UUID eventoId;

    @Column(nullable = false)
    private LocalDateTime entrada;

    private LocalDateTime saida;

    @Column(length = 50, nullable = false)
    private String status = "ATIVO";

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }

    public UUID getEventoId() { return eventoId; }
    public void setEventoId(UUID eventoId) { this.eventoId = eventoId; }

    public LocalDateTime getEntrada() { return entrada; }
    public void setEntrada(LocalDateTime entrada) { this.entrada = entrada; }

    public LocalDateTime getSaida() { return saida; }
    public void setSaida(LocalDateTime saida) { this.saida = saida; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}