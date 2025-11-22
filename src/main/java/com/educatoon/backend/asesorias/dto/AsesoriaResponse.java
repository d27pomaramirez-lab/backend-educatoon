package com.educatoon.backend.asesorias.dto;

import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
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
public class AsesoriaResponse {
    private UUID id;
    
    private String nombreEstudiante;
    private String nombreDocente;
    private String tema;
    private Date fecha;
    private Integer duracionMinutos;
    private String modalidad;
    private String ubicacion;
    private String estado;
    
    private String areasRefuerzo;
    private String observaciones;
    private Boolean asistencia;
}
