package com.educatoon.backend.academico.service;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.academico.dto.CrearHorarioRequest;
import com.educatoon.backend.academico.dto.HorarioUsuarioDTO;
import com.educatoon.backend.academico.dto.HorariosDTO;
import com.educatoon.backend.academico.model.*;
import com.educatoon.backend.academico.repository.*;
import com.educatoon.backend.usuarios.model.*;
import com.educatoon.backend.usuarios.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HorarioService {
    
    private final HorarioRepository horarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final DocenteRepository docenteRepository;
    private final DetalleMatriculaRepository detalleMatriculaRepository;
    private final SeccionRepository seccionRepository;
    
    public List<HorarioUsuarioDTO> getHorariosPorUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        // Determinar rol y obtener horarios correspondientes
        if (usuario.getRol().getNombre().equals("ROL_ESTUDIANTE")) {
            return getHorariosPorEstudiante(usuario);
        } else if (usuario.getRol().getNombre().equals("ROL_DOCENTE")) {
            return getHorariosPorDocente(usuario);
        } else {
            // Coordinador/Admin puede ver todos los horarios
            return getAllHorarios();
        }
    }
    
    private List<HorarioUsuarioDTO> getHorariosPorEstudiante(Usuario usuario) {
        Estudiante estudiante = estudianteRepository.findById(usuario.getId())
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        // Obtener secciones inscritas del estudiante
        List<DetalleMatricula> detalles = detalleMatriculaRepository.findByEstudianteId(estudiante.getId());
        
        // Filtrar solo detalles activos
        List<UUID> seccionesIds = detalles.stream()
            .filter(detalle -> "INSCRITO".equals(detalle.getEstado()) || 
                    "EN_CURSO".equals(detalle.getEstado()))
            .map(detalle -> detalle.getSeccion().getId())
            .collect(Collectors.toList());
        
        if (seccionesIds.isEmpty()) {
            return List.of();
        }
        
        // Obtener horarios de las secciones
        return horarioRepository.findBySeccionIdIn(seccionesIds).stream()
            .map(horario -> convertirADTO(horario, "ROL_ESTUDIANTE"))
            .collect(Collectors.toList());
    }
    
    private List<HorarioUsuarioDTO> getHorariosPorDocente(Usuario usuario) {
        Docente docente = docenteRepository.findById(usuario.getId())
            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        
        // Obtener secciones del docente
        List<Seccion> secciones = seccionRepository.findByDocenteId(docente.getId());
        List<UUID> seccionesIds = secciones.stream()
            .map(Seccion::getId)
            .collect(Collectors.toList());
        
        if (seccionesIds.isEmpty()) {
            return List.of();
        }
        
        // Obtener horarios de las secciones
        return horarioRepository.findBySeccionIdIn(seccionesIds).stream()
            .map(horario -> convertirADTO(horario, "ROL_DOCENTE"))
            .collect(Collectors.toList());
    }
    
    private List<HorarioUsuarioDTO> getAllHorarios() {
        return horarioRepository.findAll().stream()
            .map(horario -> convertirADTO(horario, "ROL_COORDINADOR"))
            .collect(Collectors.toList());
    }
    
    public List<HorarioUsuarioDTO> getHorariosPorUsuarioId(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return getHorariosPorUsuario(usuario.getEmail());
    }
    
    public List<HorarioUsuarioDTO> getHorariosPorEstudianteId(UUID estudianteId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        return getHorariosPorEstudiante(estudiante.getUsuario());
    }
    
    public List<HorarioUsuarioDTO> getHorariosPorDocenteId(UUID docenteId) {
        Docente docente = docenteRepository.findById(docenteId)
            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        return getHorariosPorDocente(docente.getUsuario());
    }
    
    public List<HorarioUsuarioDTO> getHorariosPorSeccionId(UUID seccionId) {
        return horarioRepository.findBySeccionId(seccionId).stream()
            .map(horario -> convertirADTO(horario, "SECCION"))
            .collect(Collectors.toList());
    }
    
    private HorarioUsuarioDTO convertirADTO(Horario horario, String tipoUsuario) {
        Seccion seccion = horario.getSeccion();
        
        return HorarioUsuarioDTO.builder()
            .id(horario.getId())
            .seccionId(seccion.getId())
            .seccionCodigo(seccion.getCodigoSeccion())
            .cursoNombre(seccion.getCurso() != null ? seccion.getCurso().getNombre() : "Sin curso")
            .aula(seccion.getAula())
            .diaSemana(horario.getDiaSemana())
            .horaInicio(horario.getHoraInicio())
            .horaFin(horario.getHoraFin())
            .tipoUsuario(tipoUsuario)
            .color(this.getColorPorCurso(seccion.getCurso()))
            .build();
    }
    
    
    private String getColorPorCurso(Curso curso) {
        if (curso == null) return "#6c757d";
        
        // Asignar colores por curso (puedes personalizar)
        Map<String, String> colores = Map.of(
            "MATEMÁTICAS", "#007bff",
            "FÍSICA", "#28a745", 
            "QUÍMICA", "#ffc107",
            "BIOLOGÍA", "#17a2b8",
            "LENGUAJE", "#dc3545",
            "RAZONAMIENTO", "#6f42c1"
        );
        
        return colores.getOrDefault(curso.getNombre().toUpperCase(), "#6c757d");
    }
}