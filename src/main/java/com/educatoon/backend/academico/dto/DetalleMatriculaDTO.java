package com.educatoon.backend.academico.dto;

/**
 *
 * @author Aldair
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleMatriculaDTO {
    
    private UUID id;
    private UUID seccionId;
    private String seccionCodigo;
    private String cursoNombre;
    private String aula;
    private String docenteNombre;
    private String estado;
    private BigDecimal notaFinal;
}