/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.educatoon.backend.academico.controller;

import com.educatoon.backend.academico.dto.SeccionConHorariosDTO;
import com.educatoon.backend.academico.service.SeccionService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Aldair
 */
@RestController
@RequestMapping("/api/secciones")
@RequiredArgsConstructor
public class SeccionController {
    
    private final SeccionService seccionService;
        
    @GetMapping("/con-horarios")
    public ResponseEntity<List<SeccionConHorariosDTO>> obtenerSeccionesConHorarios() {
        List<SeccionConHorariosDTO> secciones = seccionService.obtenerSeccionesConHorarios();
        return ResponseEntity.ok(secciones);
    }
    
    @GetMapping("/disponibles/{estudianteId}")
    public ResponseEntity<List<SeccionConHorariosDTO>> obtenerSeccionesDisponiblesParaEstudiante(
            @PathVariable UUID estudianteId) {
        try {
            List<SeccionConHorariosDTO> secciones = seccionService
                .obtenerSeccionesDisponiblesParaEstudiante(estudianteId);
            return ResponseEntity.ok(secciones);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}