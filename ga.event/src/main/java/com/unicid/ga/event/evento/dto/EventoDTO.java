package com.unicid.ga.event.evento.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para encapsular a entrada de dados do evento.
 */
public class EventoDTO {
    
    @NotBlank(message = "O título do evento não pode estar vazio")
    private String titulo;
    
    @NotBlank(message = "O local do evento não pode estar vazio")
    private String local;
    
    @NotNull(message = "A data de início é obrigatória")
    @FutureOrPresent(message = "A data do evento não pode estar no passado")
    private LocalDateTime dataInicio;
    
    @NotNull(message = "A carga horária é obrigatória")
    @Min(value = 1, message = "A carga horária deve ser de pelo menos 1 minuto")
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