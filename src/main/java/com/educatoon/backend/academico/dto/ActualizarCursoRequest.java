
package com.educatoon.backend.academico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author Diego
 */

@Data
public class ActualizarCursoRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;
    
    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    private String descripcion;
    
    @NotBlank(message = "El ciclo es obligatorio")
    @Size(max = 50, message = "El ciclo no puede exceder los 50 caracteres")
    private String ciclo;
}
