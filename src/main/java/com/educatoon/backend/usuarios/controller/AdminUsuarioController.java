
package com.educatoon.backend.usuarios.controller;

import com.educatoon.backend.usuarios.dto.ActualizarUsuarioRequest;
import com.educatoon.backend.usuarios.model.Usuario;
import com.educatoon.backend.usuarios.dto.UsuarioPendienteDTO;
import com.educatoon.backend.usuarios.service.UsuarioService;
import com.educatoon.backend.usuarios.dto.AdminCrearUsuarioRequest;
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
    public List<UsuarioPendienteDTO> getUsuariosPendientes() {
        return usuarioService.getUsuariosPendientes();
    }
    
    @GetMapping("/todos")
    public List<UsuarioPendienteDTO> getTodosLosUsuarios() {
        return usuarioService.getTodosLosUsuarios();
    }

    @PostMapping("/aprobar/{id}")
    public ResponseEntity<?> aprobarUsuario(@PathVariable UUID id) {
        try {
            Usuario usuarioAprobado = usuarioService.aprobarUsuario(id);
            return ResponseEntity.ok("Usuario " + usuarioAprobado.getEmail() + " aprobado exitosamente.");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@RequestBody AdminCrearUsuarioRequest request) {
        try {
            Usuario nuevoUsuario = usuarioService.crearUsuarioAdmin(request);
            return ResponseEntity.ok("Usuario " + nuevoUsuario.getEmail() + " creado exitosamente.");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/desactivar/{id}")
    public ResponseEntity<?> desactivarUsuario(@PathVariable UUID id) {
        try {
            usuarioService.desactivarUsuario(id);
            return ResponseEntity.ok("Usuario desactivado exitosamente.");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable UUID id) {
        try {
            UsuarioPendienteDTO usuario = usuarioService.getUsuarioById(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable UUID id, @RequestBody ActualizarUsuarioRequest request) {
        try {
            usuarioService.actualizarUsuario(id, request);
            return ResponseEntity.ok("Usuario actualizado exitosamente.");
        } catch (RuntimeException e) {            
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    
}
