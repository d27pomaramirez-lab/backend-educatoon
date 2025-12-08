package com.educatoon.backend.academico.dto;

/**
 *
 * @author Aldair
 */

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaDTO {
    private UUID id;
    private UUID estudianteId;
    private LocalDate fechaMatricula;
    private String estado;
    private String periodoAcademico;
    private String estudianteNombre;
    private String estudianteCodigo;
    private List<DetalleMatriculaDTO> detalles;
}