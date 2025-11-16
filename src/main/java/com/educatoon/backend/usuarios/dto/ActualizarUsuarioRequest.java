package com.educatoon.backend.usuarios.dto;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author Diego
 */

@Data
public class ActualizarUsuarioRequest {
    private String email;
    private String password; 

    private String nombres;           
    private String apellidos;          
    private String dni;              
    private String telefono;
    private String sexo;               
    private String estadoCivil;
    private Date fechaNacimiento;      

    private String carreraPostular;
    private String universidadPostular;
    private String colegioProcedencia;

    private String especialidad;
}
