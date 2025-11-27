package com.educatoon.backend.usuarios.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocenteResponse {
    private UUID id;
    private String nombreCompleto;
}