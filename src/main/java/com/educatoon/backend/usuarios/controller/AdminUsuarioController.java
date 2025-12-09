package com.educatoon.backend.usuarios.controller;

import com.educatoon.backend.usuarios.dto.ActualizarUsuarioRequest;
import com.educatoon.backend.usuarios.model.Usuario;
import com.educatoon.backend.usuarios.dto.UsuarioPendienteResponse;
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
    public List<UsuarioPendienteResponse> getUsuariosPendientes() {
        return usuarioService.getUsuariosPendientes();
    }

    @GetMapping("/todos")
    public List<UsuarioPendienteResponse> getTodosLosUsuarios(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) Boolean estado
    ) {
        if (busqueda != null || rol != null || estado != null) {
            return usuarioService.buscarUsuarios(busqueda, rol, estado);
        }
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

    @PutMapping("/estado/{id}")
    public ResponseEntity<?> cambiarEstadoUsuario(@PathVariable UUID id, @RequestParam boolean enabled) {
        try {
            // Llamas a un servicio unificado que maneja el cambio
            usuarioService.cambiarEstado(id, enabled);

            String mensaje = enabled ? "Usuario activado exitosamente." : "Usuario desactivado exitosamente.";
            return ResponseEntity.ok(mensaje);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable UUID id) {
        try {
            UsuarioPendienteResponse usuario = usuarioService.getUsuarioById(id);
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
