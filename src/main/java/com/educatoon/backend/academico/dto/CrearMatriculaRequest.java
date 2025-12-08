package com.educatoon.backend.academico.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 *
 * @author Aldair
 */
@Data
public class CrearMatriculaRequest {
    @NotNull(message = "El ID del estudiante es requerido")
    private UUID estudianteId;
    
    @NotNull(message = "El período académico es requerido")
    private String periodoAcademico;
    
    @NotEmpty(message = "Debe seleccionar al menos una sección")
    private List<UUID> seccionesIds;
}
