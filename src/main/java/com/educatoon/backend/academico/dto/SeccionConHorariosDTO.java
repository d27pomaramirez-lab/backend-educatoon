package com.educatoon.backend.academico.dto;

/**
 *
 * @author Aldair
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeccionConHorariosDTO {
    
    private UUID id;
    private String codigoSeccion;
    private String cursoNombre;
    private String aula;
    private String docenteNombre;
    private Integer capacidad;
    private Integer inscritos;
    private Integer cuposDisponibles;
    private List<HorarioDTO> horarios;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HorarioDTO {
        private String diaSemana;
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private String horarioFormateado;
    }
}