
package com.educatoon.backend.usuarios.controller;

import com.educatoon.backend.academico.dto.CursoResponse;
import com.educatoon.backend.academico.service.CursoService;
import com.educatoon.backend.asesorias.dto.AsesoriaResponse;
import com.educatoon.backend.asesorias.service.AsesoriaService;
import com.educatoon.backend.utils.CustomUserDetails;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Diego
 */

@RestController
@RequestMapping("/api/docente")
public class DocenteController {
    @Autowired private AsesoriaService asesoriaService;
    
    @Autowired private CursoService cursoService; 

    // Código de DocenteController.java

    @GetMapping("/mis-cursos")
    @PreAuthorize("hasRole('ROL_DOCENTE')")
    public ResponseEntity<List<CursoResponse>> listarMisCursos(Authentication authentication) {
    
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID docenteId = userDetails.getId();
    
        try {
            List<CursoResponse> cursos = cursoService.listarCursosPorDocente(docenteId);
            return ResponseEntity.ok(cursos);
        } catch (Exception e) {
            // Loguea el error para ver qué excepción está lanzando (JPQL/Mapeo)
            System.err.println("Error al listar cursos del docente: " + e.getMessage());
            e.printStackTrace(); // <-- CLAVE PARA DEPURAR
        
            // Devuelve un error 500 y una lista vacía
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }
   
    @GetMapping("/asesorias")
    public ResponseEntity<List<AsesoriaResponse>> listarAsesorias(Authentication authentication) {        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID docenteId = userDetails.getId();
        
        try {
            List<AsesoriaResponse> asesorias = asesoriaService.listarAsesoriasPorDocente(docenteId);
            return ResponseEntity.ok(asesorias);
        } catch (Exception e) {
             return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/asesorias/estado/{id}")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable UUID id, 
            @RequestParam String nuevoEstado,
            Authentication authentication) {        
        
        try {
            String message = asesoriaService.cambiarEstadoPorDocente(id, nuevoEstado);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
