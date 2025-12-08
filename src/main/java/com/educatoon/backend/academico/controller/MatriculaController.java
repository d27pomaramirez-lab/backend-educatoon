package com.educatoon.backend.academico.controller;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.academico.dto.*;
import com.educatoon.backend.academico.service.MatriculaService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/matriculas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MatriculaController {

    private final MatriculaService matriculaPeriodoService;
    
    // 1. Crear nueva matrícula
    @PostMapping
    public ResponseEntity<?> crearMatricula(@Valid @RequestBody CrearMatriculaRequest request) {
        try {
            MatriculaDTO matricula = matriculaPeriodoService.crearMatricula(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(matricula);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 2. Agregar sección a matrícula existente
    @PostMapping("/{matriculaId}/secciones")
    public ResponseEntity<?> agregarSeccionAMatricula(
            @PathVariable UUID matriculaId,
            @Valid @RequestBody AgregarSeccionRequest request) {
        try {
            DetalleMatriculaDTO detalle = matriculaPeriodoService.agregarSeccionAMatricula(matriculaId, request);
            return ResponseEntity.ok(detalle);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 3. Obtener todas las matrículas
    @GetMapping
    public ResponseEntity<List<MatriculaDTO>> obtenerTodasLasMatriculas() {
        List<MatriculaDTO> matriculas = matriculaPeriodoService.obtenerTodasLasMatriculas();
        return ResponseEntity.ok(matriculas);
    }
    
    // 4. Obtener matrícula por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMatriculaPorId(@PathVariable UUID id) {
        try {
            MatriculaDTO matricula = matriculaPeriodoService.obtenerMatriculaPorId(id);
            return ResponseEntity.ok(matricula);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // 5. Obtener matrículas por estudiante
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<MatriculaDTO>> obtenerMatriculasPorEstudiante(
            @PathVariable UUID estudianteId) {
        List<MatriculaDTO> matriculas = matriculaPeriodoService.obtenerMatriculasPorEstudiante(estudianteId);
        return ResponseEntity.ok(matriculas);
    }
    
    // 6. Actualizar estado de matrícula
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoMatricula(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarEstadoMatriculaRequest request) {
        try {
            MatriculaDTO matricula = matriculaPeriodoService.actualizarEstadoMatricula(id, request);
            return ResponseEntity.ok(matricula);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 7. Actualizar estado de detalle
    @PutMapping("/detalles/{detalleId}/estado/{nuevoEstado}")
    public ResponseEntity<?> actualizarEstadoDetalle(
            @PathVariable UUID detalleId,
            @PathVariable String nuevoEstado) {
        try {
            DetalleMatriculaDTO detalle = matriculaPeriodoService.actualizarEstadoDetalle(detalleId, nuevoEstado);
            return ResponseEntity.ok(detalle);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 8. Actualizar nota final
    @PutMapping("/detalles/{detalleId}/nota")
    public ResponseEntity<?> actualizarNotaFinal(
            @PathVariable UUID detalleId,
            @RequestParam BigDecimal nota) {
        try {
            DetalleMatriculaDTO detalle = matriculaPeriodoService.actualizarNotaFinal(detalleId, nota);
            return ResponseEntity.ok(detalle);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 9. Eliminar matrícula (cambiar estado a INACTIVA)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMatricula(@PathVariable UUID id) {
        try {
            matriculaPeriodoService.eliminarMatricula(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // 10. Retirar sección (eliminar detalle)
    @DeleteMapping("/detalles/{detalleId}")
    public ResponseEntity<?> retirarSeccion(@PathVariable UUID detalleId) {
        try {
            matriculaPeriodoService.retirarSeccion(detalleId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // 11. Obtener estudiantes disponibles para matrícula
    @GetMapping("/estudiantes-disponibles")
    public ResponseEntity<List<EstudianteDTO>> obtenerEstudiantesDisponibles(
            @RequestParam String periodoAcademico) {
        List<EstudianteDTO> estudiantes = matriculaPeriodoService
            .obtenerEstudiantesDisponibles(periodoAcademico);
        return ResponseEntity.ok(estudiantes);
    }
    
    // 12. Obtener matrículas por período académico
    @GetMapping("/periodo/{periodoAcademico}")
    public ResponseEntity<List<MatriculaDTO>> obtenerMatriculasPorPeriodo(
            @PathVariable String periodoAcademico) {
        // Implementar en servicio si lo necesitas
        return ResponseEntity.ok(List.of());
    }
}