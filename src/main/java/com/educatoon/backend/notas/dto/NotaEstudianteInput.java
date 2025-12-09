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
public class NotaEstudianteInput {
    private UUID detalleMatriculaId;
    private double notaParcial;
    private double notaFinal;
    private double promedioSimulacros;
    private String observaciones;
}
