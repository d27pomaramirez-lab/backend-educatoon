/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.educatoon.backend.academico.dto;

/**
 *
 * @author Aldair
 */

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoAsignacionDTO {
    private UUID estudianteId;
    private String estudianteNombre;
    private String perfilAprendizaje;
    private String seccionAsignada;
    private String horarioAsignado;
    private String razon;
    private boolean exitoso;
    private String mensaje;
}