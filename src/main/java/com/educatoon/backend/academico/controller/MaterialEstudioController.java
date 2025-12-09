package com.educatoon.backend.academico.controller;

import com.educatoon.backend.academico.service.MaterialEstudioService;
import com.educatoon.backend.utils.CustomUserDetails; // ¡Tu clase CustomUserDetails!
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID; // Importado para cursoId y usuarioId

@RestController
@RequestMapping("/api/materiales")
@RequiredArgsConstructor
public class MaterialEstudioController {

    private final MaterialEstudioService materialEstudioService;

    // CUS 08.1: Crear materiales de estudio
    @PostMapping("/curso/{cursoId}")
    @PreAuthorize("hasRole('DOCENTE')") // Precondición 1: Docente autenticado
    public ResponseEntity<String> crearMaterial(
            // *** CORRECCIÓN: PathVariable debe ser UUID ***
            @PathVariable UUID cursoId, 
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("archivo") MultipartFile archivo) {

        try {
            // 1. Obtener el ID del Docente autenticado (UUID)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UUID usuarioId = userDetails.getId(); 

            // 2. Llamar al servicio para guardar
            materialEstudioService.crearMaterial(cursoId, nombre, descripcion, archivo, usuarioId);

            // Paso 8: Confirmación exitosa
            return ResponseEntity.status(HttpStatus.CREATED).body("Material de estudio registrado con éxito.");

        } catch (IllegalArgumentException e) {
            // Manejo de Flujo Alternativo 1 (Tamaño superado)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de servidor al procesar el archivo.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
        }
    }
}