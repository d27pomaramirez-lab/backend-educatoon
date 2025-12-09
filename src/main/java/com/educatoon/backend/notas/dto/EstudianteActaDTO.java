package com.educatoon.backend.notas.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteActaDTO {
    private UUID detalleMatriculaId;
    private String codigoEstudiante;
    private String nombreCompleto;
     
    // Notas
    private double notaParcial;
    private double notaFinal;
    private double promedioSimulacros; 
    
    // Información adicional
    private String estado; // Nuevo (Aprobado, En riesgo, etc.)
    private String observaciones; 
}