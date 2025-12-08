package com.educatoon.backend.academico.dto;

/**
 *
 * @author Aldair
 */

import lombok.Data;


@Data
public class ActualizarMatriculaRequest {
    private String estado; // ACTIVA, INACTIVA, SUSPENDIDA
}