
package com.educatoon.backend.usuarios.dto;

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
public class AdminCrearUsuarioRequest {
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String email;
    private String password;
    private String nombreRol;
    private String especialidad; //solo si es docente
}
