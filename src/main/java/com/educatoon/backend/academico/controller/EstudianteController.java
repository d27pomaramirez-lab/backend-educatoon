package com.educatoon.backend.academico.controller;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.academico.dto.EstudianteDTO;
import com.educatoon.backend.academico.service.EstudianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {
    
    private final EstudianteService estudianteService;
    
    @GetMapping
    public ResponseEntity<List<EstudianteDTO>> obtenerTodosEstudiantes() {
        return ResponseEntity.ok(estudianteService.obtenerTodosEstudiantes());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EstudianteDTO> obtenerEstudiantePorId(@PathVariable UUID id) {
        return ResponseEntity.ok(estudianteService.obtenerEstudiantePorId(id));
    }
    
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<EstudianteDTO> obtenerEstudiantePorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(estudianteService.obtenerEstudiantePorCodigo(codigo));
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<EstudianteDTO>> buscarEstudiantesPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(estudianteService.buscarEstudiantesPorNombre(nombre));
    }
    
    @GetMapping("/busqueda-rapida")
    public ResponseEntity<List<EstudianteDTO>> busquedaRapida(
            @RequestParam(required = false) String termino,
            @RequestParam(defaultValue = "5") int limit) {
        
        List<EstudianteDTO> resultados = estudianteService
            .busquedaRapida(termino, limit);
        return ResponseEntity.ok(resultados);
    }
    
    @GetMapping("/disponibles-matricula")
    public ResponseEntity<List<EstudianteDTO>> getEstudiantesDisponiblesParaMatricula(
            @RequestParam String periodoAcademico,
            @RequestParam(required = false) String termino) {
        
        List<EstudianteDTO> estudiantes = estudianteService
            .getEstudiantesDisponiblesParaMatricula(periodoAcademico, termino);
        return ResponseEntity.ok(estudiantes);
    }
}