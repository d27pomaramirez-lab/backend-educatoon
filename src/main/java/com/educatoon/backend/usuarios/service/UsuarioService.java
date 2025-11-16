
package com.educatoon.backend.usuarios.service;

import com.educatoon.backend.usuarios.dto.ActualizarUsuarioRequest;
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
import java.util.stream.Collectors;
import com.educatoon.backend.usuarios.dto.UsuarioPendienteDTO;

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
        nuevoPerfil.setSexo(request.getSexo());                     
        nuevoPerfil.setEstadoCivil(request.getEstadoCivil());      
        nuevoPerfil.setFechaNacimiento(request.getFechaNacimiento());
        nuevoPerfil.setUsuario(usuarioGuardado);    
        perfilRepository.save(nuevoPerfil);
        
        Estudiante nuevoEstudiante = new Estudiante();
        nuevoEstudiante.setUsuario(usuarioGuardado);
        nuevoEstudiante.setFechaMatricula(new Date());
        nuevoEstudiante.setCarreraPostular(request.getCarreraPostular());   
        nuevoEstudiante.setUniversidadPostular(request.getUniversidadPostular()); 
        nuevoEstudiante.setColegioProcedencia(request.getColegioProcedencia());        
        estudianteRepository.save(nuevoEstudiante);
        
        return usuarioGuardado;
    }
    
    @Transactional
    public Usuario aprobarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        if (usuario.getRol() != null && usuario.getRol().getNombre().equals("ROL_ESTUDIANTE")) {

            Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Datos de estudiante no encontrados para el usuario: " + id));

            if (!estudiante.isDocumentosValidados()) {
                throw new RuntimeException("Error: Los documentos de este estudiante aún no han sido validados por un Coordinador.");
            }

            if (estudiante.getCodigoEstudiante() == null || estudiante.getCodigoEstudiante().isEmpty()) {
                String codigoGenerado = "E" + (new Date().getYear() + 1900) + "-" + id.toString().substring(0, 4).toUpperCase();
                estudiante.setCodigoEstudiante(codigoGenerado);
                estudianteRepository.save(estudiante);
            }
        }
        usuario.setEnabled(true);
        return usuarioRepository.save(usuario);
    }   
    
    public List<UsuarioPendienteDTO> getUsuariosPendientes() {
        List<Usuario> usuariosInactivos = usuarioRepository.findByEnabled(false);
        
        List<Usuario> soloPendientes = usuariosInactivos.stream()
            .filter(usuario -> {
                if (usuario.getRol() == null || !usuario.getRol().getNombre().equals("ROL_ESTUDIANTE")) {
                    return false;
                }               
                Estudiante estudiante = estudianteRepository.findById(usuario.getId()).orElse(null);
                
                return (estudiante != null && estudiante.getCodigoEstudiante() == null);
            })
            .collect(Collectors.toList());
            
        return soloPendientes.stream()
            .map(this::convertirAUsuarioPendienteDTO)
            .collect(Collectors.toList());
    }
    
    private UsuarioPendienteDTO convertirAUsuarioPendienteDTO(Usuario usuario) {
        UsuarioPendienteDTO dto = new UsuarioPendienteDTO();

        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setEnabled(usuario.isEnabled());
        
        if (usuario.getRol() != null) {
            dto.setRolNombre(usuario.getRol().getNombre());
        }

        if (usuario.getPerfil() != null) {
            dto.setNombres(usuario.getPerfil().getNombres());
            dto.setApellidos(usuario.getPerfil().getApellidos());
            dto.setTelefono(usuario.getPerfil().getTelefono());
            dto.setSexo(usuario.getPerfil().getSexo());
            dto.setDni(usuario.getPerfil().getDni());
            dto.setEstadoCivil(usuario.getPerfil().getEstadoCivil());
            dto.setFechaNacimiento(usuario.getPerfil().getFechaNacimiento());
        }

        if (usuario.getRol() != null && usuario.getRol().getNombre().equals("ROL_ESTUDIANTE")) {
            Estudiante estudiante = estudianteRepository.findById(usuario.getId()).orElse(null);
            if (estudiante != null) {
                dto.setDocumentosValidados(estudiante.isDocumentosValidados());
                dto.setCarreraPostular(estudiante.getCarreraPostular());
                dto.setUniversidadPostular(estudiante.getUniversidadPostular());
                dto.setColegioProcedencia(estudiante.getColegioProcedencia());
            }
        }

        if (usuario.getRol() != null && usuario.getRol().getNombre().equals("ROL_DOCENTE")) {
            Docente docente = docenteRepository.findById(usuario.getId()).orElse(null);
            if (docente != null) {
                dto.setEspecialidad(docente.getEspecialidad());
            }
        }
        return dto;
    }
    
    @Transactional
    public void validarDocumentos(UUID id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        if(usuario.getRol() == null || !usuario.getRol().getNombre().equals("ROL_ESTUDIANTE")){
            throw new RuntimeException("Este usuario no es un estudiante");            
        }
        
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Datos de estudiante no encontrados para el usuario: " + id));
        
        estudiante.setDocumentosValidados(true);
        estudianteRepository.save(estudiante);
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
        nuevoPerfil.setSexo(request.getSexo());                   
        nuevoPerfil.setEstadoCivil(request.getEstadoCivil());       
        nuevoPerfil.setFechaNacimiento(request.getFechaNacimiento());
        nuevoPerfil.setUsuario(usuarioGuardado);
        perfilRepository.save(nuevoPerfil);

        if (rol.getNombre().equals("ROL_DOCENTE")) {
            Docente nuevoDocente = new Docente();
            nuevoDocente.setUsuario(usuarioGuardado);
            nuevoDocente.setEspecialidad(request.getEspecialidad());
            docenteRepository.save(nuevoDocente);
        }
        
        if (rol.getNombre().equals("ROL_ESTUDIANTE")) {
            Estudiante nuevoEstudiante = new Estudiante();
            nuevoEstudiante.setUsuario(usuarioGuardado);
            nuevoEstudiante.setFechaMatricula(new Date());
            nuevoEstudiante.setDocumentosValidados(true);
            nuevoEstudiante.setCarreraPostular(request.getCarreraPostular());     
            nuevoEstudiante.setUniversidadPostular(request.getUniversidadPostular()); 
            nuevoEstudiante.setColegioProcedencia(request.getColegioProcedencia()); 
            estudianteRepository.save(nuevoEstudiante);
        }
        return usuarioGuardado;
    }
    
    public List<UsuarioPendienteDTO> getTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAllAndFetchPerfil();

        return usuarios.stream()
            .map(this::convertirAUsuarioPendienteDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void desactivarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        usuario.setEnabled(false);
        usuarioRepository.save(usuario);
    }
    
    public UsuarioPendienteDTO getUsuarioById(UUID id) {
        Usuario usuario = usuarioRepository.findByIdAndFetchPerfil(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        return convertirAUsuarioPendienteDTO(usuario);
    }
    
    @Transactional
    public Usuario actualizarUsuario(UUID id, ActualizarUsuarioRequest request) {
        
        Usuario usuario = usuarioRepository.findByIdAndFetchPerfil(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (!usuario.getEmail().equals(request.getEmail()) && 
                usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("Error: El nuevo email ya está en uso.");
            }
            usuario.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (usuario.getPerfil() != null) {
            Perfil perfil = usuario.getPerfil();
            perfil.setNombres(request.getNombres());
            perfil.setApellidos(request.getApellidos());
            perfil.setDni(request.getDni());
            perfil.setTelefono(request.getTelefono());
            perfil.setSexo(request.getSexo());
            perfil.setEstadoCivil(request.getEstadoCivil());
            perfil.setFechaNacimiento(request.getFechaNacimiento());
        }

        if (usuario.getRol() != null) {            
            if (usuario.getRol().getNombre().equals("ROL_ESTUDIANTE")) {
                Estudiante estudiante = estudianteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Datos de estudiante no encontrados para actualizar."));
                
                estudiante.setCarreraPostular(request.getCarreraPostular());
                estudiante.setUniversidadPostular(request.getUniversidadPostular());
                estudiante.setColegioProcedencia(request.getColegioProcedencia());
            }
            
            if (usuario.getRol().getNombre().equals("ROL_DOCENTE")) {
                Docente docente = docenteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Datos de docente no encontrados para actualizar."));
                
                docente.setEspecialidad(request.getEspecialidad());
            }
        }
        return usuarioRepository.save(usuario);
    }
    
}
