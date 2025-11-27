
package com.educatoon.backend.academico.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author hecto
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeccionRequest {
    private String curso;
    private int capacidad;
    private String aula;
    private String docente;
}
