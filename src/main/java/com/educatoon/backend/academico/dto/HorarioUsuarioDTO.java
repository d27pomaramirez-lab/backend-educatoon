package com.educatoon.backend.academico.dto;

/**
 *
 * @author Aldair
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioUsuarioDTO {
    private UUID id;
    private UUID seccionId;
    private String seccionCodigo;
    private String cursoNombre;
    private String aula;
    private String diaSemana; // "LUNES", "MARTES", etc.
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String tipoUsuario; // "ESTUDIANTE", "DOCENTE", "TODOS"
    private String color; // Para el calendario
}