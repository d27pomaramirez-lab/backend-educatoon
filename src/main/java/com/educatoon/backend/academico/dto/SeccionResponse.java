// Puedes crear un nuevo archivo SeccionResponse.java
package com.educatoon.backend.academico.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class SeccionResponse {
    private UUID id;
    private String codigoSeccion;
    private int capacidad;
    private String aula;
    private String curso;
    private String docente; // Nombre del docente
}