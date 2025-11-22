package com.educatoon.backend.usuarios.dto;

import java.util.Date;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Diego
 */

@Data
@NoArgsConstructor
public class UsuarioPendienteResponse {
    private UUID id;
    private String email;

    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String sexo;
    private String estadoCivil;      
    private Date fechaNacimiento;

    private String rolNombre;
    
    private boolean documentosValidados;
    private boolean enabled;
    
    private String carreraPostular;    
    private String universidadPostular; 
    private String colegioProcedencia; 

    private String especialidad;       
    
}
