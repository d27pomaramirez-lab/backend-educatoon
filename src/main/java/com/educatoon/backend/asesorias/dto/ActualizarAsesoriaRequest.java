package com.educatoon.backend.asesorias.dto;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author Diego
 */

@Data
public class ActualizarAsesoriaRequest {
    private Date fecha; 
    private Integer duracionMinutos;
    
    private String modalidad; 
    private String enlaceReunion;
    private String lugarPresencial;
    
    private String tema;
    private String areasRefuerzo;
}
