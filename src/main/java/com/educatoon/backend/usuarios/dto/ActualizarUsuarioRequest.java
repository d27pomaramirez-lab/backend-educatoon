package com.educatoon.backend.usuarios.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Diego
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
