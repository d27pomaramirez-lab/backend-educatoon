
package com.educatoon.backend.usuarios.service;

import com.educatoon.backend.usuarios.model.Perfil;
import com.educatoon.backend.usuarios.repository.PerfilRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 *
 * @author Aldair
 */



@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository perfilRepository;

    @Value("${app.upload.dir:uploads/perfiles}")
    private String uploadDir;

    // Obtener perfil del usuario logueado
    public Perfil getPerfilByUsuarioId(UUID usuarioId) {
        return perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
    }

    // Subir foto de perfil
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

    // Obtener foto
    public byte[] obtenerFotoPerfil(String nombreArchivo) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(nombreArchivo);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la foto");
        }
    }

    // Eliminar foto
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

    private String obtenerExtension(String filename) {
        if (filename == null) return "jpg";
        int lastDot = filename.lastIndexOf(".");
        return lastDot == -1 ? "jpg" : filename.substring(lastDot + 1);
    }
}
