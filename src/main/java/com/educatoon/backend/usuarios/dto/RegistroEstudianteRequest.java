
package com.educatoon.backend.usuarios.dto;

import lombok.Data;

/**
 *
 * @author Diego
 */

@Data
public class RegistroEstudianteRequest {
    private String email;
    private String password;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
}
