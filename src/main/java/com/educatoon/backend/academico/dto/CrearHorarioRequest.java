/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.educatoon.backend.academico.dto;

/**
 *
 * @author Aldair
 */
// CrearHorarioRequest.java

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class CrearHorarioRequest {
    @NotNull(message = "La sección es requerida")
    private UUID seccionId;
    
    @NotBlank(message = "El día de la semana es requerido")
    private String diaSemana; // "LUNES", "MARTES", etc.
    
    @NotNull(message = "La hora de inicio es requerida")
    private LocalTime horaInicio;
    
    @NotNull(message = "La hora de fin es requerida")
    private LocalTime horaFin;
}