/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.educatoon.backend.usuarios.dto;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author Aldair
 */
@Data
public class PerfilUpdateDTO {
    private String telefono;
    private String sexo;
    private String estadoCivil;
    private Date fechaNacimiento;
    
    // Solo para estudiantes
    private String carreraPostular;
    private String universidadPostular;
    private String colegioProcedencia;
    
    // Solo para docentes
    private String especialidad;
}