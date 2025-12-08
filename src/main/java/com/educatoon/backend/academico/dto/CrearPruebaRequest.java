package com.educatoon.backend.academico.dto;

/**
 *
 * @author Aldair
 */

import lombok.Data;
import java.util.UUID;


@Data
public class CrearPruebaRequest {
    private UUID estudianteId;
    private Double puntajeTotal;
    private String perfilAprendizaje;
}