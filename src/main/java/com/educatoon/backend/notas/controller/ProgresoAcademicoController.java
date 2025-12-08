package com.educatoon.backend.notas.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educatoon.backend.notas.dto.ProgresoResumenDTO;
import com.educatoon.backend.notas.service.ProgresoAcademicoService;

@RestController
@RequestMapping("/api/estudiante")
public class ProgresoAcademicoController {

    @Autowired
    private ProgresoAcademicoService progresoService;

    @GetMapping("/progreso-academico/{dni}")
    public ResponseEntity<?> consultarProgreso(@PathVariable String dni) {
        try {
            List<ProgresoResumenDTO> progreso = progresoService.obtenerProgresoPorDni(dni);

            if (progreso.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("mensaje", "No se encontraron cursos matriculados o registros de notas para este estudiante."));
            }

            return ResponseEntity.ok(progreso);
            
        } catch (RuntimeException e) {
            // Manejo de error si el DNI no existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
