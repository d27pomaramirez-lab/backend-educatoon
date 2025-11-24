package com.educatoon.backend.usuarios.service;

import com.educatoon.backend.usuarios.dto.PerfilResponse;
import com.educatoon.backend.usuarios.model.Docente;
import com.educatoon.backend.usuarios.model.Estudiante;
import com.educatoon.backend.usuarios.model.Perfil;
import com.educatoon.backend.usuarios.model.Usuario;
import com.educatoon.backend.usuarios.repository.DocenteRepository;
import com.educatoon.backend.usuarios.repository.EstudianteRepository;
import com.educatoon.backend.usuarios.repository.PerfilRepository;
import com.educatoon.backend.usuarios.repository.UsuarioRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final DocenteRepository docenteRepository;

    @Value("${app.upload.dir:uploads/perfiles}")
    private String uploadDir;

    // Obtener perfil por EMAIL
    public PerfilResponse getPerfilByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return getPerfilCompleto(usuario.getId());
    }

    // Obtener perfil por ID
    public PerfilResponse getPerfilCompleto(UUID usuarioId) {
        // 1. Obtener perfil básico
        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
        
        // 2. Obtener usuario para email y rol
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        PerfilResponse response = new PerfilResponse();
        
        // Datos del perfil
        response.setNombres(perfil.getNombres());
        response.setApellidos(perfil.getApellidos());
        response.setDni(perfil.getDni());
        response.setTelefono(perfil.getTelefono());
        response.setFotoPerfil(perfil.getFoto());
        response.setSexo(perfil.getSexo());
        response.setEstadoCivil(perfil.getEstadoCivil());
        response.setFechaNacimiento(perfil.getFechaNacimiento());
        
        // Datos del usuario
        response.setEmail(usuario.getEmail());
        response.setRol(usuario.getRol().getNombre());
        
        // 3. Buscar si es estudiante
        Optional<Estudiante> estudiante = estudianteRepository.findById(usuarioId);
        if (estudiante.isPresent()) {
            response.setCodigoEstudiante(estudiante.get().getCodigoEstudiante());
            response.setFechaMatricula(estudiante.get().getFechaMatricula());
            response.setCarreraPostular(estudiante.get().getCarreraPostular());
            response.setUniversidadPostular(estudiante.get().getUniversidadPostular());
            response.setColegioProcedencia(estudiante.get().getColegioProcedencia());
        }
        
        // 4. Buscar si es docente
        Optional<Docente> docente = docenteRepository.findById(usuarioId);
        if (docente.isPresent()) {
            response.setEspecialidad(docente.get().getEspecialidad());
        }
        
        return response;
    }

    // Subir foto por EMAIL
    public String actualizarFotoPerfilByEmail(String email, MultipartFile foto) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return actualizarFotoPerfil(usuario.getId(), foto);
    }

    // Subir foto por ID
    public String actualizarFotoPerfil(UUID usuarioId, MultipartFile foto) {
        Perfil perfil = getPerfilByUsuarioId(usuarioId);

        try {
            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Validar que sea imagen
            if (!foto.getContentType().startsWith("image/")) {
                throw new RuntimeException("Solo se permiten archivos de imagen");
            }

            // Generar nombre único
            String extension = obtenerExtension(foto.getOriginalFilename());
            String nombreArchivo = "perfil_" + usuarioId + "_" + System.currentTimeMillis() + "." + extension;
            Path filePath = uploadPath.resolve(nombreArchivo);

            // Guardar archivo
            Files.copy(foto.getInputStream(), filePath);

            // Eliminar foto anterior si existe
            if (perfil.getFoto() != null) {
                Path fotoAnterior = uploadPath.resolve(perfil.getFoto());
                Files.deleteIfExists(fotoAnterior);
            }

            // Guardar solo el nombre del archivo
            perfil.setFoto(nombreArchivo);
            perfilRepository.save(perfil);

            return nombreArchivo;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la foto: " + e.getMessage());
        }
    }

    // Eliminar foto por EMAIL
    public void eliminarFotoPerfilByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        eliminarFotoPerfil(usuario.getId());
    }

    // Eliminar foto por ID
    public void eliminarFotoPerfil(UUID usuarioId) {
        Perfil perfil = getPerfilByUsuarioId(usuarioId);

        if (perfil.getFoto() != null) {
            try {
                Path filePath = Paths.get(uploadDir).resolve(perfil.getFoto());
                Files.deleteIfExists(filePath);
                perfil.setFoto(null);
                perfilRepository.save(perfil);
            } catch (IOException e) {
                throw new RuntimeException("Error al eliminar la foto");
            }
        }
    }

    // Obtener foto
    public byte[] obtenerFotoPerfil(String nombreArchivo) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(nombreArchivo);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la foto");
        }
    }

    // Método auxiliar
    public Perfil getPerfilByUsuarioId(UUID usuarioId) {
        return perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
    }

    private String obtenerExtension(String filename) {
        if (filename == null) return "jpg";
        int lastDot = filename.lastIndexOf(".");
        return lastDot == -1 ? "jpg" : filename.substring(lastDot + 1);
    }
}