package com.educatoon.backend.academico.controller;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.academico.dto.CrearHorarioRequest;
import com.educatoon.backend.academico.dto.HorarioUsuarioDTO;
import com.educatoon.backend.academico.dto.HorariosDTO;
import com.educatoon.backend.academico.service.HorarioCrudService;
import com.educatoon.backend.academico.service.HorarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HorarioController{
    
    private final HorarioService horarioService;
    private final HorarioCrudService horarioCrudService;
    
    @PostMapping
    public ResponseEntity<HorariosDTO> crearHorario(@Valid @RequestBody CrearHorarioRequest request) {
        try {
            HorariosDTO horario = horarioCrudService.crearHorario(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(horario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HorariosDTO> actualizarHorario(
            @PathVariable UUID id, 
            @Valid @RequestBody CrearHorarioRequest request) {
        try {
            HorariosDTO horario = horarioCrudService.actualizarHorario(id, request);
            return ResponseEntity.ok(horario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHorario(@PathVariable UUID id) {
        try {
            horarioCrudService.eliminarHorario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/secciones/{seccionId}")
    public ResponseEntity<List<HorariosDTO>> getHorariosPorSecciones(@PathVariable UUID seccionId) {
        List<HorariosDTO> horarios = horarioCrudService.getHorariosPorSeccion(seccionId);
        return ResponseEntity.ok(horarios);
    }
    
    // Obtener horarios del usuario autenticado (según su rol)
    @GetMapping("/mi-horario")
    public ResponseEntity<List<HorarioUsuarioDTO>> getMiHorario(@AuthenticationPrincipal UserDetails userDetails) {
        List<HorarioUsuarioDTO> horarios = horarioService.getHorariosPorUsuario(userDetails.getUsername());
        return ResponseEntity.ok(horarios);
    }
    
    // Obtener horarios por usuario específico (para coordinador)
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<HorarioUsuarioDTO>> getHorariosPorUsuario(@PathVariable UUID usuarioId) {
        List<HorarioUsuarioDTO> horarios = horarioService.getHorariosPorUsuarioId(usuarioId);
        return ResponseEntity.ok(horarios);
    }
    
    // Obtener horarios por estudiante
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<HorarioUsuarioDTO>> getHorariosPorEstudiante(@PathVariable UUID estudianteId) {
        List<HorarioUsuarioDTO> horarios = horarioService.getHorariosPorEstudianteId(estudianteId);
        return ResponseEntity.ok(horarios);
    }
    
    // Obtener horarios por docente
    @GetMapping("/docente/{docenteId}")
    public ResponseEntity<List<HorarioUsuarioDTO>> getHorariosPorDocente(@PathVariable UUID docenteId) {
        List<HorarioUsuarioDTO> horarios = horarioService.getHorariosPorDocenteId(docenteId);
        return ResponseEntity.ok(horarios);
    }
    
    // Obtener horarios por sección
    @GetMapping("/seccion/{seccionId}")
    public ResponseEntity<List<HorarioUsuarioDTO>> getHorariosPorSeccion(@PathVariable UUID seccionId) {
        List<HorarioUsuarioDTO> horarios = horarioService.getHorariosPorSeccionId(seccionId);
        return ResponseEntity.ok(horarios);
    }
}