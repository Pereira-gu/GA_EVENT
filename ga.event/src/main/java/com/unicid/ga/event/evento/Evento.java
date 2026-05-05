package com.unicid.ga.event.evento;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

//Model da tabela Evento
@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String local;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "carga_horaria", nullable = false)
    private Integer cargaHoraria; // Carga horária em minutos

    // Construtores
    public Evento() {}

    public Evento(String titulo, String local, LocalDateTime dataInicio, Integer cargaHoraria) {
        this.titulo = titulo;
        this.local = local;
        this.dataInicio = dataInicio;
        this.cargaHoraria = cargaHoraria;
    }

//  Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public Integer getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; }
}

