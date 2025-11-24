package com.educatoon.backend.usuarios.dto;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author Aldair
 */
@Data
public class PerfilResponse {
    // Datos básicos del perfil
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String fotoPerfil;
    private String sexo;
    private String estadoCivil;
    private Date fechaNacimiento;
    private String email;
    private String rol;
    
    // Datos específicos de ESTUDIANTE
    private String codigoEstudiante;
    private Date fechaMatricula;
    private String carreraPostular;
    private String universidadPostular;
    private String colegioProcedencia;
    
    // Datos específicos de DOCENTE
    private String especialidad;
}