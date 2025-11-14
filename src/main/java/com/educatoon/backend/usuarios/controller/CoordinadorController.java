package com.educatoon.backend.usuarios.controller;

import com.educatoon.backend.usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

/**
 *
 * @author Diego
 */

@RestController
@RequestMapping("/api/coordinador")
public class CoordinadorController {
    
    @Autowired private UsuarioService usuarioService;
    
    @PostMapping("/validar-documentos/{id}")
    public ResponseEntity<?> validarDocumentosEstudiante(@PathVariable UUID id){
        try {
            usuarioService.validarDocumentos(id);
            return ResponseEntity.ok("Documentos del estudiante validados exitosamente.");                    
        }catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }
}
