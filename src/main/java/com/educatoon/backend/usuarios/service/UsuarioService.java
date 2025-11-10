
package com.educatoon.backend.usuarios.service;

import com.educatoon.backend.usuarios.dto.RegistroEstudianteRequest;
import com.educatoon.backend.usuarios.model.Estudiante;
import com.educatoon.backend.usuarios.model.Perfil;
import com.educatoon.backend.usuarios.model.Rol;
import com.educatoon.backend.usuarios.model.Usuario;
import com.educatoon.backend.usuarios.repository.EstudianteRepository;
import com.educatoon.backend.usuarios.repository.PerfilRepository;
import com.educatoon.backend.usuarios.repository.RolRepository;
import com.educatoon.backend.usuarios.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.educatoon.backend.usuarios.dto.AdminCrearUsuarioRequest;
import com.educatoon.backend.usuarios.model.Docente;
import com.educatoon.backend.usuarios.repository.DocenteRepository;

/**
 *
 * @author Diego
 */

@Service
public class UsuarioService {
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;    
    @Autowired private PerfilRepository perfilRepository;
    @Autowired private EstudianteRepository estudianteRepository;
    @Autowired private DocenteRepository docenteRepository;
    
    @Autowired private PasswordEncoder passwordEncoder;
    
    @Transactional
    public Usuario solicitarRegistroEstudiante(RegistroEstudianteRequest request){
        if(usuarioRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Error: El email ya está en uso!");
        }
        
        Rol rolEstudiante = rolRepository.findByNombre("ROL_ESTUDIANTE").
                orElseThrow(() -> new RuntimeException("Error: Rol 'ROL_ESTUDIANTE' no econtrado."));
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setRol(rolEstudiante);
        nuevoUsuario.setEnabled(false);
        
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        
        Perfil nuevoPerfil = new Perfil();
        nuevoPerfil.setNombres(request.getNombres());
        nuevoPerfil.setApellidos(request.getApellidos());
        nuevoPerfil.setDni(request.getDni());
        nuevoPerfil.setTelefono(request.getTelefono());
        nuevoPerfil.setUsuario(usuarioGuardado);
        
        perfilRepository.save(nuevoPerfil);
        
        Estudiante nuevoEstudiante = new Estudiante();
        nuevoEstudiante.setUsuario(usuarioGuardado);
        nuevoEstudiante.setFechaMatricula(new Date());
        
        estudianteRepository.save(nuevoEstudiante);
        
        return usuarioGuardado;
    }
    
    public Usuario aprobarUsuario(UUID id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no econtrado con id: " + id));        
        usuario.setEnabled(true);
        
        if(usuario.getRol() != null && usuario.getRol().getNombre().equals("ROL_ESTUDIANTE")){
            Estudiante estudiante = estudianteRepository.findById(id)
                    .orElseThrow(()-> new RuntimeException("Datos de estudiante no encontrados para el usuario: " + id));
            String codigoGenerado = "E" + (new Date().getYear() + 1900) + "-" + id.toString().substring(0,4).toUpperCase();
            
            estudiante.setCodigoEstudiante(codigoGenerado);
            estudianteRepository.save(estudiante);
        }
        
        return usuarioRepository.save(usuario);
    }
    
    public List<Usuario> getUsuariosPendientes(){
        return usuarioRepository.findByEnabled(false);
    }
    
    @Transactional
    public Usuario crearUsuarioAdmin(AdminCrearUsuarioRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Error: El email ya está en uso!");
        }

        Rol rol = rolRepository.findByNombre(request.getNombreRol())
            .orElseThrow(() -> new RuntimeException("Error: Rol '" + request.getNombreRol() + "' no encontrado."));

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setEnabled(true);
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        Perfil nuevoPerfil = new Perfil();
        nuevoPerfil.setNombres(request.getNombres());
        nuevoPerfil.setApellidos(request.getApellidos());
        nuevoPerfil.setDni(request.getDni());
        nuevoPerfil.setTelefono(request.getTelefono());
        nuevoPerfil.setUsuario(usuarioGuardado);
        perfilRepository.save(nuevoPerfil);

        if (rol.getNombre().equals("ROL_DOCENTE")) {
            Docente nuevoDocente = new Docente();
            nuevoDocente.setUsuario(usuarioGuardado);
            nuevoDocente.setEspecialidad(request.getEspecialidad());
            docenteRepository.save(nuevoDocente);
        }

        return usuarioGuardado;
    }
}
