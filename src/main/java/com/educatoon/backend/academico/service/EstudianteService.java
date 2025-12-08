/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.educatoon.backend.academico.service;

import com.educatoon.backend.academico.dto.EstudianteDTO;
import com.educatoon.backend.academico.repository.MatriculaRepository;
import com.educatoon.backend.usuarios.model.Estudiante;
import com.educatoon.backend.usuarios.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Mejora performance para operaciones de lectura
public class EstudianteService {
    
    private final EstudianteRepository estudianteRepository;
    
    private final MatriculaRepository matriculaRepository;
    
    public List<EstudianteDTO> obtenerTodosEstudiantes() {
        // Usa el método optimizado
        return estudianteRepository.findAllWithUsuarioAndPerfil().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public EstudianteDTO obtenerEstudiantePorId(UUID id) {
        // Para un solo estudiante, también optimiza
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con id: " + id));
        
        // Carga eager las relaciones necesarias
        return convertirADTO(estudiante);
    }
    
    public EstudianteDTO obtenerEstudiantePorCodigo(String codigo) {
        Estudiante estudiante = estudianteRepository.findByCodigoEstudiante(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con código: " + codigo));
        return convertirADTO(estudiante);
    }
    
        public List<EstudianteDTO> busquedaRapida(String termino, int limit) {
        if (termino == null || termino.trim().isEmpty()) {
            return List.of();
        }
        
        String terminoBusqueda = "%" + termino.toLowerCase() + "%";
        
        return estudianteRepository.busquedaRapida(terminoBusqueda, limit).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public List<EstudianteDTO> getEstudiantesDisponiblesParaMatricula(
            String periodoAcademico, String termino) {
        
        // Obtener todos los estudiantes que coincidan con el término
        List<Estudiante> estudiantes;
        if (termino != null && !termino.trim().isEmpty()) {
            String terminoBusqueda = "%" + termino.toLowerCase() + "%";
            estudiantes = estudianteRepository.busquedaRapida(terminoBusqueda, 20);
        } else {
            estudiantes = estudianteRepository.findAllWithUsuarioAndPerfil();
        }
        
        // Obtener IDs de estudiantes ya matriculados en el período
        List<UUID> estudiantesMatriculadosIds = matriculaRepository
            .findByPeriodoAcademico(periodoAcademico).stream()
            .map(mp -> mp.getEstudiante().getId())
            .collect(Collectors.toList());
        
        return estudiantes.stream()
            .map(e -> {
                boolean yaMatriculado = estudiantesMatriculadosIds.contains(e.getId());
                String periodoMatriculado = null;
                
                if (yaMatriculado) {
                    periodoMatriculado = periodoAcademico;
                }
                
                return EstudianteDTO.builder()
                    .id(e.getId())
                    .codigoEstudiante(e.getCodigoEstudiante())
                    .nombreCompleto(e.getUsuario().getPerfil().getNombres() + " " + 
                                   e.getUsuario().getPerfil().getApellidos())
                    .carreraPostular(e.getCarreraPostular())
                    .fechaMatricula(e.getFechaMatricula())
                    .documentosValidados(e.isDocumentosValidados())
                    .matriculadoActual(yaMatriculado)
                    .periodoAcademicoActual(periodoMatriculado)
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    public List<EstudianteDTO> buscarEstudiantesPorNombre(String nombre) {
        // Ya está optimizado en el repositorio
        String nombreBusqueda = "%" + nombre.toLowerCase() + "%";
        return estudianteRepository.buscarPorNombreCompleto(nombreBusqueda).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Método para obtener solo datos básicos (si no necesitas toda la información)
    public List<EstudianteDTO> obtenerEstudiantesBasicos() {
        return estudianteRepository.findAll().stream()
                .map(e -> EstudianteDTO.builder()
                    .id(e.getId())
                    .codigoEstudiante(e.getCodigoEstudiante())
                    .nombreCompleto(e.getCodigoEstudiante()) // Código como placeholder
                    .build())
                .collect(Collectors.toList());
    }
    
    private EstudianteDTO convertirADTO(Estudiante estudiante) {
        String nombreCompleto = "";
        
        // Verificar null pointers
        if (estudiante.getUsuario() != null && 
            estudiante.getUsuario().getPerfil() != null) {
            
            String nombres = estudiante.getUsuario().getPerfil().getNombres() != null ? 
                           estudiante.getUsuario().getPerfil().getNombres() : "";
            String apellidos = estudiante.getUsuario().getPerfil().getApellidos() != null ? 
                             estudiante.getUsuario().getPerfil().getApellidos() : "";
            
            nombreCompleto = (nombres + " " + apellidos).trim();
        } else {
            nombreCompleto = "Nombre no disponible";
        }
        
        return EstudianteDTO.builder()
                .id(estudiante.getId())
                .nombreCompleto(nombreCompleto)
                .codigoEstudiante(estudiante.getCodigoEstudiante())
                .fechaMatricula(estudiante.getFechaMatricula())
                .documentosValidados(estudiante.isDocumentosValidados())
                .carreraPostular(estudiante.getCarreraPostular())
                .universidadPostular(estudiante.getUniversidadPostular())
                .colegioProcedencia(estudiante.getColegioProcedencia())
                .build();
    }
    
    // Si necesitas un DTO más simple para combobox/select
    public List<EstudianteDTO> obtenerEstudiantesParaSelect() {
        return estudianteRepository.findAllWithUsuarioAndPerfil().stream()
                .map(e -> EstudianteDTO.builder()
                    .id(e.getId())
                    .codigoEstudiante(e.getCodigoEstudiante())
                    .nombreCompleto(e.getCodigoEstudiante() + " - " + 
                                  e.getUsuario().getPerfil().getNombres() + " " +
                                  e.getUsuario().getPerfil().getApellidos())
                    .build())
                .collect(Collectors.toList());
    }
}