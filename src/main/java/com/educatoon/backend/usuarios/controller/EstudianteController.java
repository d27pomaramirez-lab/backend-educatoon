package com.educatoon.backend.usuarios.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Diego
 */

@RestController
@RequestMapping("/api/estudiante")
public class EstudianteController {
    @Autowired private AsesoriaService asesoriaService;
    
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
}
