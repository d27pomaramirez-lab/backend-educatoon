
package com.educatoon.backend.usuarios.controller;

import com.educatoon.backend.usuarios.model.Usuario;
import com.educatoon.backend.usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Diego
 */

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/pendientes")
    public List<Usuario> getUsuariosPendientes() {
        return usuarioService.getUsuariosPendientes();
    }

    // Endpoint para aprobar un usuario (este SÍ es PostMapping)
    @PostMapping("/aprobar/{id}")
    public ResponseEntity<?> aprobarUsuario(@PathVariable UUID id) { // Cambiado a UUID
        try {
            Usuario usuarioAprobado = usuarioService.aprobarUsuario(id);
            return ResponseEntity.ok("Usuario " + usuarioAprobado.getEmail() + " aprobado exitosamente.");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
