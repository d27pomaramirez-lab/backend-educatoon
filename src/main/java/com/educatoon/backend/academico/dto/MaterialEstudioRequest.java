
package com.educatoon.backend.academico.dto;

/**
 *
 * @author giuse
 */

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class MaterialEstudioRequest {
    
    // El ID del curso al que pertenece el material
    private Long cursoId;

    // Nombre del material
    private String nombre;

    // Descripción del material
    private String descripcion;

    // Nota: El archivo (MultipartFile) no se incluye aquí, se manejará directamente en el Controller y Service.
}