
package com.educatoon.backend.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 *
 * @author Diego
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEstudianteRequest {
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
}
