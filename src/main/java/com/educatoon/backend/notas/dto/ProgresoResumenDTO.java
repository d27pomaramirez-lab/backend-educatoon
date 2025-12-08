package com.educatoon.backend.notas.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgresoResumenDTO {
    private String nombreCurso;
    private String codigoSeccion;
    private String nombreDocente; 

    private double notaParcial;
    private Double notaFinal;
    private double promedioSimulacros;

    private double avance; // Para la barra de carga
    private String estado; // "Aprobado", "En riesgo", etc.
    private String observaciones;
    private LocalDateTime ultimaActualizacion;
}
