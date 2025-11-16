package com.educatoon.backend.usuarios.dto;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Diego
 */

@Data
@NoArgsConstructor
public class UsuarioPendienteDTO {
    private UUID id;
    private String email;

    private String nombres;
    private String apellidos;
    private String telefono;
    private String sexo;

    private String rolNombre;
    
    private boolean documentosValidados;
    private boolean enabled;
    
}
