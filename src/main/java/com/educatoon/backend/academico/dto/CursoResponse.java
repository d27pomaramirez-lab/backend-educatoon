
package com.educatoon.backend.academico.dto;

import java.util.UUID;
import lombok.Data;

/**
 *
 * @author Diego
 */

@Data
public class CursoResponse {
    private UUID id;
    private String nombre;
    private String descripcion;
    private String ciclo;
    private boolean estado;   
}
