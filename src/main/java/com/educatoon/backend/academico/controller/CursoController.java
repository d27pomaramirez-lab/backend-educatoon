
package com.educatoon.backend.academico.controller;

import com.educatoon.backend.academico.dto.ActualizarCursoRequest;
import com.educatoon.backend.academico.dto.CursoRequest;
import com.educatoon.backend.academico.dto.CursoResponse;
import com.educatoon.backend.academico.service.CursoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Diego
 */

@RestController
@RequestMapping("/api/cursos")
public class CursoController {
    @Autowired
    private CursoService cursoService;

    @PostMapping("/crear")
    public ResponseEntity<CursoResponse> crearCurso(@Valid @RequestBody CursoRequest request) {
        CursoResponse newCurso = cursoService.crearCurso(request);
        return new ResponseEntity<>(newCurso, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<CursoResponse>> listarTodos() {
        List<CursoResponse> cursos = cursoService.listarTodos();
        return ResponseEntity.ok(cursos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponse> obtenerPorId(@PathVariable UUID id) {
        CursoResponse curso = cursoService.obtenerPorId(id);
        return ResponseEntity.ok(curso);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<CursoResponse> actualizarCurso(
            @PathVariable UUID id, 
            @Valid @RequestBody ActualizarCursoRequest request) {
        CursoResponse updatedCurso = cursoService.actualizarCurso(id, request);
        return ResponseEntity.ok(updatedCurso);
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<String> cambiarEstado(
            @PathVariable UUID id, 
            @RequestParam boolean activo) {
        String message = cursoService.cambiarEstado(id, activo);
        return ResponseEntity.ok(message);
    }
}
