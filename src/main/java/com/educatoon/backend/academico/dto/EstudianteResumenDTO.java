package com.educatoon.backend.academico.dto;

import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteResumenDTO {
    private UUID id;
    private String nombreCompleto;
    private String codigoEstudiante;
    private Date fechaMatricula;
    private boolean matriculadoActual;
}