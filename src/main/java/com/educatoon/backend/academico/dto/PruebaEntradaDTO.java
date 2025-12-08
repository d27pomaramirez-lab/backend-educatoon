
package com.educatoon.backend.academico.dto;

/**
 *
 * @author Aldair
 */

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PruebaEntradaDTO {
    private UUID id;
    private UUID estudianteId;
    private String estudianteNombre;
    private String estudianteCodigo;
    private LocalDate fechaRendicion;
    private Double puntajeTotal;
    private String perfilAprendizaje;
    private String estado;
}
