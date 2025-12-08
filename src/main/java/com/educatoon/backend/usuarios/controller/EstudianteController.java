package com.educatoon.backend.usuarios.controller;

import com.educatoon.backend.academico.dto.EstudianteDTO;
import com.educatoon.backend.academico.service.EstudianteService;
import com.educatoon.backend.asesorias.dto.AsesoriaResponse;
import com.educatoon.backend.asesorias.service.AsesoriaService;
import com.educatoon.backend.utils.CustomUserDetails;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Diego
 */

@RestController
@RequestMapping("/api/estudiante")
public class EstudianteController {
    @Autowired private AsesoriaService asesoriaService;
    @Autowired EstudianteService estudianteService;

    @GetMapping("/asesorias")
    public ResponseEntity<List<AsesoriaResponse>> listarAsesorias(Authentication authentication) {        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID estudianteId = userDetails.getId(); 
        
        try {
            List<AsesoriaResponse> asesorias = asesoriaService.listarAsesoriasPorEstudiante(estudianteId);
            return ResponseEntity.ok(asesorias);
        } catch (Exception e) {
             return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
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
