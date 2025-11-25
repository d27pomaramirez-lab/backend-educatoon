package com.educatoon.backend.usuarios.controller;

/**
 *
 * @author Aldair
 */
import com.educatoon.backend.usuarios.dto.PerfilResponse;
import com.educatoon.backend.usuarios.dto.PerfilUpdateDTO;
import com.educatoon.backend.usuarios.service.PerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PerfilController {

    private final PerfilService perfilService;

    // Obtener perfil completo por EMAIL
    @GetMapping("/by-email/{email}")
    public ResponseEntity<PerfilResponse> getPerfilByEmail(@PathVariable String email) {
        try {
            PerfilResponse perfil = perfilService.getPerfilByEmail(email);
            return ResponseEntity.ok(perfil);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Actualizar perfil por EMAIL
    @PutMapping("/by-email/{email}")
    public ResponseEntity<PerfilResponse> actualizarPerfilByEmail(
            @PathVariable String email,
            @RequestBody PerfilUpdateDTO perfilUpdateDTO) {
        try {
            PerfilResponse perfilActualizado = perfilService.actualizarPerfilByEmail(email, perfilUpdateDTO);
            return ResponseEntity.ok(perfilActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // Obtener perfil completo por ID (mantener por si acaso)
    @GetMapping("/{usuarioId}")
    public ResponseEntity<PerfilResponse> getPerfil(@PathVariable UUID usuarioId) {
        try {
            PerfilResponse perfil = perfilService.getPerfilCompleto(usuarioId);
            return ResponseEntity.ok(perfil);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Subir foto de perfil por EMAIL
    @PostMapping("/by-email/{email}/foto")
    public ResponseEntity<?> subirFotoPerfilByEmail(
            @PathVariable String email,
            @RequestParam("foto") MultipartFile foto) {
        try {
            String nombreArchivo = perfilService.actualizarFotoPerfilByEmail(email, foto);
            return ResponseEntity.ok(nombreArchivo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Subir foto de perfil por ID
    @PostMapping("/{usuarioId}/foto")
    public ResponseEntity<?> subirFotoPerfil(
            @PathVariable UUID usuarioId,
            @RequestParam("foto") MultipartFile foto) {
        try {
            String nombreArchivo = perfilService.actualizarFotoPerfil(usuarioId, foto);
            return ResponseEntity.ok(nombreArchivo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener foto (para mostrar en <img src>)
    @GetMapping("/foto/{nombreArchivo}")
    public ResponseEntity<byte[]> obtenerFotoPerfil(@PathVariable String nombreArchivo) {
        try {
            byte[] fotoBytes = perfilService.obtenerFotoPerfil(nombreArchivo);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(fotoBytes.length);
            headers.setCacheControl("max-age=3600");
            
            return new ResponseEntity<>(fotoBytes, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar foto de perfil por EMAIL
    @DeleteMapping("/by-email/{email}/foto")
    public ResponseEntity<?> eliminarFotoPerfilByEmail(@PathVariable String email) {
        try {
            perfilService.eliminarFotoPerfilByEmail(email);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Eliminar foto de perfil por ID
    @DeleteMapping("/{usuarioId}/foto")
    public ResponseEntity<?> eliminarFotoPerfil(@PathVariable UUID usuarioId) {
        try {
            perfilService.eliminarFotoPerfil(usuarioId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}