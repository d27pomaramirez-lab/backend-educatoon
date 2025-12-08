
package com.educatoon.backend.academico.controller;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.academico.dto.*;
import com.educatoon.backend.academico.service.PruebaEntradaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pruebas-entrada")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PruebaEntradaController {

    private final PruebaEntradaService pruebaEntradaService;

    @PostMapping
    public ResponseEntity<?> registrarPrueba(@RequestBody CrearPruebaRequest request) {
        try {
            PruebaEntradaDTO prueba = pruebaEntradaService.registrarPrueba(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(prueba);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{pruebaId}/asignar-aula")
    public ResponseEntity<?> asignarAula(@PathVariable UUID pruebaId) {
        try {
            // En una implementación real, buscaríamos la prueba por ID
            // Por ahora usamos el flujo automático desde registrarPrueba
            return ResponseEntity.badRequest().body("Use POST /api/pruebas-entrada para asignación automática");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PruebaEntradaDTO>> obtenerPruebasPorEstado(@PathVariable String estado) {
        List<PruebaEntradaDTO> pruebas = pruebaEntradaService.obtenerPruebasPorEstado(estado);
        return ResponseEntity.ok(pruebas);
    }

    @GetMapping("/asignaciones/estado/{estado}")
    public ResponseEntity<List<AsignacionAulaDTO>> obtenerAsignacionesPorEstado(@PathVariable String estado) {
        List<AsignacionAulaDTO> asignaciones = pruebaEntradaService.obtenerAsignacionesPorEstado(estado);
        return ResponseEntity.ok(asignaciones);
    }

    @GetMapping("/asignaciones/estudiante/{estudianteId}")
    public ResponseEntity<?> obtenerAsignacionPorEstudiante(@PathVariable UUID estudianteId) {
        return pruebaEntradaService.obtenerAsignacionPorEstudiante(estudianteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reporte-asignaciones")
    public ResponseEntity<List<AsignacionAulaDTO>> obtenerReporteAsignaciones() {
        List<AsignacionAulaDTO> asignaciones = pruebaEntradaService.obtenerAsignacionesPorEstado("ASIGNADO");
        return ResponseEntity.ok(asignaciones);
    }
}