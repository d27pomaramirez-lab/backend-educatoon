package com.educatoon.backend.academico.dto;

/**
 *
 * @author Aldair
 */

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarSeccionRequest {
    
    @NotNull(message = "El ID de la sección es requerido")
    private UUID seccionId;
}