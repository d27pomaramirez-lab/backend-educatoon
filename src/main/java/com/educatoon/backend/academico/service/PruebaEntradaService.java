package com.educatoon.backend.academico.service;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.academico.dto.*;
import com.educatoon.backend.academico.model.*;
import com.educatoon.backend.academico.repository.*;
import com.educatoon.backend.usuarios.model.Estudiante;
import com.educatoon.backend.usuarios.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PruebaEntradaService {

    private final PruebaEntradaRepository pruebaEntradaRepository;
    private final AsignacionAulaRepository asignacionAulaRepository;
    private final HorarioRepository horarioRepository;
    private final SeccionRepository seccionRepository;
    private final EstudianteRepository estudianteRepository;
    private final MatriculaService matriculaService;

    @Transactional
    public PruebaEntradaDTO registrarPrueba(CrearPruebaRequest request) {
        Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        if (pruebaEntradaRepository.existsByEstudianteId(estudiante.getId())) { // ← getId()
            throw new RuntimeException("El estudiante ya tiene una prueba registrada");
        }

        PruebaEntrada prueba = new PruebaEntrada();
        prueba.setEstudiante(estudiante);
        prueba.setFechaRendicion(LocalDate.now());
        prueba.setPuntajeTotal(request.getPuntajeTotal());
        prueba.setPerfilAprendizaje(request.getPerfilAprendizaje());
        prueba.setEstado("EVALUADO");

        PruebaEntrada pruebaGuardada = pruebaEntradaRepository.save(prueba);
        
        // Intentar asignación automática
        //ResultadoAsignacionDTO resultado = asignarAulaAutomaticamente(pruebaGuardada);
        
        return convertirPruebaADTO(pruebaGuardada);
    }

    private String obtenerNombreEstudiante(Estudiante estudiante) {
        if (estudiante != null && 
            estudiante.getUsuario() != null && 
            estudiante.getUsuario().getPerfil() != null) {
            return estudiante.getUsuario().getPerfil().getNombres() + " " + 
                   estudiante.getUsuario().getPerfil().getApellidos();
        }
        return "Nombre no disponible";
    }

private Horario seleccionarHorarioPorPerfil(List<Horario> horarios, String perfil, Double puntajeTotal) {
    // Primero: Filtrar por perfil si es posible
    List<Horario> horariosFiltrados = new ArrayList<>();
    
    for (Horario horario : horarios) {
        String codigoSeccion = horario.getSeccion().getCodigoSeccion();
        String nombreCurso = horario.getSeccion().getCurso().getNombre().toUpperCase();
        
        // Lógica basada en perfil de aprendizaje
        boolean coincidePerfil = false;
        
        switch (perfil) {
            case "VISUAL":
                coincidePerfil = nombreCurso.contains("ALGEBRA") || nombreCurso.contains("GEOMETRIA") ||
                        nombreCurso.contains("MATEMATICA");
                break;
            case "AUDITIVO":
                coincidePerfil = nombreCurso.contains("LENGUAJE") || nombreCurso.contains("HISTORIA") ||
                        nombreCurso.contains("LITERATURA");
                break;
            default:
                // Perfil mixto o no especificado
                coincidePerfil = true;
                break;
        }
        
        if (coincidePerfil) {
            horariosFiltrados.add(horario);
        }
    }
    
    // Si no hay horarios para el perfil, usar todos los disponibles
    if (horariosFiltrados.isEmpty()) {
        horariosFiltrados = horarios;
    }
    
    // Segundo: Asignar sección según puntaje
    if (puntajeTotal != null) {
        return asignarPorPuntaje(horariosFiltrados, puntajeTotal);
    }
    
    // Si no hay puntaje, devolver el primero disponible
    return horariosFiltrados.isEmpty() ? null : horariosFiltrados.get(0);
}

private Horario asignarPorPuntaje(List<Horario> horarios, Double puntajeTotal) {
    // Ordenar horarios por código de sección (A, B, C, D o 1, 2, 3, 4)
    List<Horario> horariosOrdenados = horarios.stream()
            .sorted(Comparator.comparing(h -> h.getSeccion().getCodigoSeccion()))
            .collect(Collectors.toList());
    
    // Definir rangos de puntaje para cada sección
    int totalSecciones = horariosOrdenados.size();
    if (totalSecciones == 0) return null;
    
    // Asignar según percentil del puntaje
    double puntajeMaximo = 100.0; // Asumiendo que el puntaje máximo es 100
    double percentil = (puntajeTotal / puntajeMaximo) * totalSecciones;
    
    int indiceSeccion = Math.min((int) Math.floor(percentil), totalSecciones - 1);
    
    return horariosOrdenados.get(indiceSeccion);
}

private String generarRazonAsignacion(PruebaEntrada prueba, Horario horario, Double puntajeTotal) {
    String razonPerfil = String.format("Perfil de aprendizaje: %s", prueba.getPerfilAprendizaje());
    String razonPuntaje = puntajeTotal != null ? 
            String.format("Puntaje obtenido: %.2f/100", puntajeTotal) : "Puntaje no disponible";
    String razonSeccion = String.format("Sección asignada: %s (%s)", 
            horario.getSeccion().getCodigoSeccion(), 
            horario.getSeccion().getCurso().getNombre());
    String razonHorario = String.format("Horario: %s %s-%s", 
            horario.getDiaSemana(), horario.getHoraInicio(), horario.getHoraFin());
    
    return String.format("%s. %s. %s. %s", 
            razonPerfil, razonPuntaje, razonSeccion, razonHorario);
}

    private String formatHorario(Horario horario) {
        return String.format("%s %s-%s",
            horario.getDiaSemana(),
            horario.getHoraInicio(),
            horario.getHoraFin()
        );
    }

    public List<PruebaEntradaDTO> obtenerPruebasPorEstado(String estado) {
        return pruebaEntradaRepository.findByEstado(estado).stream()
                .map(this::convertirPruebaADTO)
                .collect(Collectors.toList());
    }

    public List<AsignacionAulaDTO> obtenerAsignacionesPorEstado(String estado) {
        return asignacionAulaRepository.findByEstado(estado).stream()
                .map(this::convertirAsignacionADTO)
                .collect(Collectors.toList());
    }

    public Optional<AsignacionAulaDTO> obtenerAsignacionPorEstudiante(UUID estudianteId) {
        return asignacionAulaRepository.findByEstudianteId(estudianteId)
                .map(this::convertirAsignacionADTO);
    }

    private PruebaEntradaDTO convertirPruebaADTO(PruebaEntrada prueba) {
        PruebaEntradaDTO dto = new PruebaEntradaDTO();
        dto.setId(prueba.getId());
        dto.setEstudianteId(prueba.getEstudiante().getId()); // ← getId()
        dto.setEstudianteNombre(obtenerNombreEstudiante(prueba.getEstudiante()));
        dto.setEstudianteCodigo(prueba.getEstudiante().getCodigoEstudiante());
        dto.setFechaRendicion(prueba.getFechaRendicion());
        dto.setPuntajeTotal(prueba.getPuntajeTotal());
        dto.setPerfilAprendizaje(prueba.getPerfilAprendizaje());
        dto.setEstado(prueba.getEstado());
        return dto;
    }

    private AsignacionAulaDTO convertirAsignacionADTO(AsignacionAula asignacion) {
        AsignacionAulaDTO dto = new AsignacionAulaDTO();
        dto.setId(asignacion.getId());
        dto.setEstudianteId(asignacion.getEstudiante().getId()); // ← getId()
        dto.setEstudianteNombre(obtenerNombreEstudiante(asignacion.getEstudiante()));
        dto.setEstudianteCodigo(asignacion.getEstudiante().getCodigoEstudiante());
        dto.setSeccionId(asignacion.getSeccion().getId());
        dto.setSeccionCodigo(asignacion.getSeccion().getCodigoSeccion());
        dto.setCursoNombre(asignacion.getSeccion().getCurso().getNombre());
        dto.setHorarioId(asignacion.getHorario().getId());
        dto.setHorario(formatHorario(asignacion.getHorario()));
        dto.setRazonAsignacion(asignacion.getRazonAsignacion());
        dto.setEstado(asignacion.getEstado());
        return dto;
    }
}