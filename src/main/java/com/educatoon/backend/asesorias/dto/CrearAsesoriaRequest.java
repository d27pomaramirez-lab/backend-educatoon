package com.educatoon.backend.asesorias.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Diego
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearAsesoriaRequest {
    private UUID estudianteId;
    private UUID docenteId;
    private UUID cursoId;
    private Date fecha;
    private Integer duracionMinutos;
    private String modalidad;
    private String enlaceReunion;
    private String lugarPresencial;
    private String tema;
    private String areasRefuerzo;
}
