package com.educatoon.backend.notas.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educatoon.backend.notas.dto.EstudianteActaDTO;
import com.educatoon.backend.notas.dto.RegistroNotasRequest;
import com.educatoon.backend.notas.service.RegistroNotasService;

@RestController
@RequestMapping("/api/coordinador/notas")
public class RegistroNotasController {
    @Autowired
    private RegistroNotasService registroNotasService;

    // Endpoint para cargar la tabla de notas (Acta)
    @GetMapping("/acta/{seccionId}")
    public ResponseEntity<List<EstudianteActaDTO>> obtenerActa(@PathVariable UUID seccionId) {
        return ResponseEntity.ok(registroNotasService.obtenerActaPorSeccion(seccionId));
    }

    // Endpoint para guardar las notas ingresadas
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarNotas(@RequestBody RegistroNotasRequest request) {
        try {
            registroNotasService.registrarNotas(request);
            return ResponseEntity.ok("Notas registradas exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar notas: " + e.getMessage());
        }
    }
}
