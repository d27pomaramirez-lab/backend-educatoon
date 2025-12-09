package com.educatoon.backend.academico.service;

import com.educatoon.backend.academico.model.MaterialEstudio;
import com.educatoon.backend.academico.repository.MaterialEstudioRepository;
import com.educatoon.backend.usuarios.repository.UsuarioRepository;
import com.educatoon.backend.usuarios.model.Usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialEstudioService {

    private final MaterialEstudioRepository materialEstudioRepository;
    private final UsuarioRepository usuarioRepository; 

    // Define la ruta base para guardar archivos. ¡Añadir en application.properties!
    @Value("${app.upload.materiales-dir:/uploads/materiales}")
    private String uploadDir;
    
    // 50 MB (para manejar el Flujo Alternativo 1: Tamaño superado)
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; 

    @Transactional
    // *** CORRECCIÓN: cursoId y usuarioId son de tipo UUID ***
    public MaterialEstudio crearMaterial(UUID cursoId, String nombre, String descripcion, MultipartFile archivo, UUID usuarioId) throws IOException {
        
        // --- Paso 6: El sistema valida el archivo (Flujo Alternativo 1) ---
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío.");
        }
        
        // 1. Validación de tamaño 
        if (archivo.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Tamaño superado: El archivo excede el límite de " + (MAX_FILE_SIZE / 1024 / 1024) + "MB.");
        }

        // 2. Determinar la ruta y nombre de archivo seguro
        String originalFilename = archivo.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String safeFilename = "material_" + UUID.randomUUID().toString() + extension;
        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(safeFilename);

        // 3. Guardar el archivo físicamente
        Files.createDirectories(uploadPath); 
        archivo.transferTo(filePath.toFile());

        // 4. Obtener el usuario autenticado (se asume que UsuarioRepository usa UUID)
        Usuario docente = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Docente no encontrado o no autorizado."));

        // 5. Crear la entidad MaterialEstudio (Paso 7: El sistema guarda el material)
        MaterialEstudio material = MaterialEstudio.builder()
                .cursoId(cursoId) // UUID
                .nombre(nombre)
                .descripcion(descripcion)
                .rutaArchivo(safeFilename) 
                .tipoMime(archivo.getContentType())
                .usuarioSubida(docente)
                .fechaSubida(LocalDateTime.now())
                .build();

        return materialEstudioRepository.save(material);
    }
}