/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.educatoon.backend.academico.dto;

/**
 *
 * @author Aldair
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteDTO {
    private UUID id;
    private String nombreCompleto;
    private String codigoEstudiante;
    private Date fechaMatricula;
    private boolean documentosValidados;
    private String carreraPostular;
    private String universidadPostular;
    private String colegioProcedencia;
    private String periodoAcademicoActual;
    private boolean matriculadoActual;
}