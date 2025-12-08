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
import java.util.UUID;

@Data  
public class AsignacionAulaDTO {
    private UUID id;
    private UUID estudianteId;
    private String estudianteNombre;
    private String estudianteCodigo;
    private UUID seccionId;
    private String seccionCodigo;
    private String cursoNombre;
    private UUID horarioId;
    private String horario; // "LUNES 08:00-10:00"
    private String razonAsignacion;
    private String estado;
}
