
package com.educatoon.backend.security.controller;

import com.educatoon.backend.usuarios.model.Usuario;
import com.educatoon.backend.usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 *
 * @author Diego
 */

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity
                .badRequest()
                .body("Error: El email ya está en uso!");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));        
        
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuario registrado exitosamente!");
    }

    // Endpoint para CUS 02: Iniciar Sesión (simplificado)
    // Spring Security ya maneja el login, esto es para generar el token (siguiente paso)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        // En un proyecto real, la autenticación la hace el filtro de Spring.
        // Aquí generarías y devolverías el JWT.
        
        // Por ahora, solo confirmamos que el usuario existe (simulación)
        String email = credentials.get("email");
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
        
        // TO-DO: Generar y devolver token JWT
        return ResponseEntity.ok("Login exitoso (JWT no implementado)");
    }
}
