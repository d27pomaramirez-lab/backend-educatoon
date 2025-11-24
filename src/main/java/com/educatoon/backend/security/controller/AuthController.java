
package com.educatoon.backend.security.controller;

import com.educatoon.backend.usuarios.dto.RegistroEstudianteRequest;
import com.educatoon.backend.security.dto.LoginRequest;
import com.educatoon.backend.security.jwt.JwtUtil;
import com.educatoon.backend.security.service.UserDetailsServiceImpl;
import com.educatoon.backend.usuarios.service.UsuarioService;
import com.educatoon.backend.usuarios.model.Usuario;
import com.educatoon.backend.usuarios.repository.UsuarioRepository;
import com.educatoon.backend.security.dto.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Diego
 */

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService; 
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @PostMapping("/register-student")
    public ResponseEntity<?> registerStudent(@RequestBody RegistroEstudianteRequest request) {
        try {
            usuarioService.solicitarRegistroEstudiante(request);
            return ResponseEntity.ok("¡Solicitud de registro enviada! Espere aprobación del administrador.");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        Usuario usuarioCompleto = usuarioRepository.findByEmailAndFetchPerfil(loginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("Error al cargar datos de perfil de usuario."));
        String nombres = (usuarioCompleto.getPerfil() != null) ? usuarioCompleto.getPerfil().getNombres() : "";
        String apellidos = (usuarioCompleto.getPerfil() != null) ? usuarioCompleto.getPerfil().getApellidos() : "";

        return ResponseEntity.ok(
            new JwtResponse(
                token, 
                userDetails.getUsername(), 
                userDetails.getAuthorities(), 
                nombres, 
                apellidos
            )
        );
    }
    
    
}
