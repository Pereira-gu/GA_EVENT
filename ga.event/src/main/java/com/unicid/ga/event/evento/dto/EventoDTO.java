package com.unicid.ga.event.evento.dto;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para encapsular a entrada de dados do evento.
 */
public class EventoDTO {
    private String titulo;
    private String local;
    private LocalDateTime dataInicio;
    private Integer cargaHoraria;

    // Getters e Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public Integer getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; }
}