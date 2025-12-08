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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MatriculaService {
    
    private final MatriculaRepository matriculaPeriodoRepository;
    private final DetalleMatriculaRepository detalleMatriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final SeccionRepository seccionRepository;
    private final HorarioRepository horarioRepository;
    
    // 1. Crear nueva matrícula con múltiples secciones
    public MatriculaDTO crearMatricula(CrearMatriculaRequest request) {
        // 1- Validar estudiante
        Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + request.getEstudianteId()));
        
        // 2- Validar que no tenga matrícula activa en el mismo período
        if (matriculaPeriodoRepository.existsByEstudianteIdAndPeriodoAcademico(
                estudiante.getId(), request.getPeriodoAcademico())) {
            throw new RuntimeException("El estudiante ya tiene una matrícula activa para el período " + 
                                      request.getPeriodoAcademico());
        }
        
        // 3- Validar horarios ANTES de crear detalles
        validarHorariosNoSolapados(estudiante.getId(), request.getSeccionesIds());
        
        // 4- Validar y obtener secciones
        List<Seccion> secciones = new ArrayList<>();
        for (UUID seccionId : request.getSeccionesIds()) {
            Seccion seccion = seccionRepository.findById(seccionId)
                .orElseThrow(() -> new RuntimeException("Sección no encontrada: " + seccionId));
            
            // Validar capacidad de la sección
            Long inscritos = detalleMatriculaRepository.countBySeccionIdAndEstadoIn(
                seccionId, 
                Arrays.asList("INSCRITO", "EN_CURSO")
            );
            
            if (inscritos >= seccion.getCapacidad()) {
                throw new RuntimeException("La sección " + seccion.getCodigoSeccion() + 
                                          " ha alcanzado su capacidad máxima (" + seccion.getCapacidad() + ")");
            }
            validarCapacidadSeccion(seccion);
            secciones.add(seccion);
        }
        
        // 5- Crear matrícula y detalles
        Matricula matricula = crearMatriculaConDetalles(
            estudiante, request.getPeriodoAcademico(), secciones
        );
        
        return convertirADTO(matricula);
    }
    
    // 2. Agregar sección a matrícula existente
    public DetalleMatriculaDTO agregarSeccionAMatricula(UUID matriculaId, AgregarSeccionRequest request) {
        Matricula matricula = matriculaPeriodoRepository.findById(matriculaId)
            .orElseThrow(() -> new RuntimeException("Matrícula no encontrada con ID: " + matriculaId));
        
        // Validar que la matrícula esté activa
        if (!"ACTIVA".equals(matricula.getEstado())) {
            throw new RuntimeException("No se puede agregar secciones a una matrícula " + matricula.getEstado());
        }
        
        Seccion seccion = seccionRepository.findById(request.getSeccionId())
            .orElseThrow(() -> new RuntimeException("Sección no encontrada con ID: " + request.getSeccionId()));
        
        // Validar que no esté ya inscrito en esta sección
        boolean yaInscrito = detalleMatriculaRepository.existsByEstudianteAndSeccion(
            matricula.getEstudiante().getId(),
            seccion.getId(),
            Arrays.asList("INSCRITO", "EN_CURSO")
        );
        
        if (yaInscrito) {
            throw new RuntimeException("El estudiante ya está inscrito en esta sección");
        }
        
        // Validar capacidad
        Long inscritos = detalleMatriculaRepository.countBySeccionIdAndEstadoIn(
            seccion.getId(),
            Arrays.asList("INSCRITO", "EN_CURSO")
        );
        
        if (inscritos >= seccion.getCapacidad()) {
            throw new RuntimeException("La sección ha alcanzado su capacidad máxima");
        }
        
        // Crear detalle
        DetalleMatricula detalle = DetalleMatricula.builder()
            .matricula(matricula)
            .seccion(seccion)
            .estado("INSCRITO")
            .build();
        
        DetalleMatricula detalleGuardado = detalleMatriculaRepository.save(detalle);
        
        return convertirDetalleADTO(detalleGuardado);
    }
    
    // 3. Obtener todas las matrículas
    @Transactional(readOnly = true)
    public List<MatriculaDTO> obtenerTodasLasMatriculas() {
        return matriculaPeriodoRepository.findAllWithDetalles().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    // 4. Obtener matrícula por ID
    @Transactional(readOnly = true)
    public MatriculaDTO obtenerMatriculaPorId(UUID id) {
        Matricula matricula = matriculaPeriodoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Matrícula no encontrada con ID: " + id));
        return convertirADTO(matricula);
    }
    
    // 5. Obtener matrículas por estudiante
    @Transactional(readOnly = true)
    public List<MatriculaDTO> obtenerMatriculasPorEstudiante(UUID estudianteId) {
        return matriculaPeriodoRepository.findByEstudianteId(estudianteId).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    // 6. Actualizar estado de matrícula
    public MatriculaDTO actualizarEstadoMatricula(UUID id, ActualizarEstadoMatriculaRequest request) {
        Matricula matricula = matriculaPeriodoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Matrícula no encontrada con ID: " + id));
        
        matricula.setEstado(request.getEstado());
        Matricula actualizada = matriculaPeriodoRepository.save(matricula);
        
        return convertirADTO(actualizada);
    }
    
    // 7. Actualizar estado de detalle (retirar/aprobar curso)
    public DetalleMatriculaDTO actualizarEstadoDetalle(UUID detalleId, String nuevoEstado) {
        DetalleMatricula detalle = detalleMatriculaRepository.findById(detalleId)
            .orElseThrow(() -> new RuntimeException("Detalle de matrícula no encontrado con ID: " + detalleId));
        
        detalle.setEstado(nuevoEstado);
        DetalleMatricula actualizado = detalleMatriculaRepository.save(detalle);
        
        return convertirDetalleADTO(actualizado);
    }
    
    // 8. Actualizar nota final de un detalle
    public DetalleMatriculaDTO actualizarNotaFinal(UUID detalleId, BigDecimal notaFinal) {
        DetalleMatricula detalle = detalleMatriculaRepository.findById(detalleId)
            .orElseThrow(() -> new RuntimeException("Detalle de matrícula no encontrado con ID: " + detalleId));
        
        // Validar nota
        if (notaFinal.compareTo(BigDecimal.ZERO) < 0 || notaFinal.compareTo(new BigDecimal("20")) > 0) {
            throw new RuntimeException("La nota debe estar entre 0 y 20");
        }
        
        detalle.setNotaFinal(notaFinal);
        
        // Si la nota es >= 11, marcar como APROBADO automáticamente
        if (notaFinal.compareTo(new BigDecimal("11")) >= 0) {
            detalle.setEstado("APROBADO");
        } else {
            detalle.setEstado("DESAPROBADO");
        }
        
        DetalleMatricula actualizado = detalleMatriculaRepository.save(detalle);
        
        return convertirDetalleADTO(actualizado);
    }
    
    // 9. Eliminar matrícula (cambiar estado a INACTIVA)
    public void eliminarMatricula(UUID id) {
        Matricula matricula = matriculaPeriodoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Matrícula no encontrada con ID: " + id));
        
        matricula.setEstado("INACTIVA");
        matriculaPeriodoRepository.save(matricula);
    }
    
    // 10. Eliminar detalle de matrícula (retirar curso)
    public void retirarSeccion(UUID detalleId) {
        DetalleMatricula detalle = detalleMatriculaRepository.findById(detalleId)
            .orElseThrow(() -> new RuntimeException("Detalle de matrícula no encontrado con ID: " + detalleId));
        
        detalle.setEstado("RETIRADO");
        detalleMatriculaRepository.save(detalle);
    }
    
    // 11. Obtener estudiantes disponibles para matrícula (no matriculados en período actual)
    @Transactional(readOnly = true)
    public List<EstudianteDTO> obtenerEstudiantesDisponibles(String periodoAcademico) {
        List<Estudiante> todosEstudiantes = estudianteRepository.findAllWithUsuarioAndPerfil();
        
        // Obtener IDs de estudiantes ya matriculados en el período
        List<UUID> estudiantesMatriculadosIds = matriculaPeriodoRepository
            .findByPeriodoAcademico(periodoAcademico).stream()
            .map(mp -> mp.getEstudiante().getId())
            .collect(Collectors.toList());
        
        return todosEstudiantes.stream()
            .map(estudiante -> {
                boolean matriculado = estudiantesMatriculadosIds.contains(estudiante.getId());
                
                return EstudianteDTO.builder()
                    .id(estudiante.getId())
                    .codigoEstudiante(estudiante.getCodigoEstudiante())
                    .nombreCompleto(estudiante.getUsuario().getPerfil().getNombres() + " " + 
                                   estudiante.getUsuario().getPerfil().getApellidos())
                    .carreraPostular(estudiante.getCarreraPostular())
                    .fechaMatricula(estudiante.getFechaMatricula())
                    .documentosValidados(estudiante.isDocumentosValidados())
                    .matriculadoActual(matriculado)
                    .periodoAcademicoActual(matriculado ? periodoAcademico : null)
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    // Métodos de conversión DTO
    private MatriculaDTO convertirADTO(Matricula matricula) {
        String nombreEstudiante = matricula.getEstudiante().getUsuario().getPerfil().getNombres() + " " +
                                 matricula.getEstudiante().getUsuario().getPerfil().getApellidos();
        
        List<DetalleMatriculaDTO> detallesDTO = matricula.getDetalles().stream()
            .map(this::convertirDetalleADTO)
            .collect(Collectors.toList());
        
        return MatriculaDTO.builder()
            .id(matricula.getId())
            .estudianteId(matricula.getEstudiante().getId())
            .estudianteNombre(nombreEstudiante.trim())
            .estudianteCodigo(matricula.getEstudiante().getCodigoEstudiante())
            .periodoAcademico(matricula.getPeriodoAcademico())
            .fechaMatricula(matricula.getFechaMatricula())
            .estado(matricula.getEstado())
            .detalles(detallesDTO)
            .build();
    }
    
    private DetalleMatriculaDTO convertirDetalleADTO(DetalleMatricula detalle) {
        Seccion seccion = detalle.getSeccion();
        String nombreDocente = "No asignado";
        
        if (seccion.getDocente() != null && seccion.getDocente().getUsuario() != null &&
            seccion.getDocente().getUsuario().getPerfil() != null) {
            nombreDocente = seccion.getDocente().getUsuario().getPerfil().getNombres() + " " +
                           seccion.getDocente().getUsuario().getPerfil().getApellidos();
        }
        
        return DetalleMatriculaDTO.builder()
            .id(detalle.getId())
            .seccionId(seccion.getId())
            .seccionCodigo(seccion.getCodigoSeccion())
            .cursoNombre(seccion.getCurso() != null ? seccion.getCurso().getNombre() : "Sin curso")
            .aula(seccion.getAula())
            .docenteNombre(nombreDocente)
            .estado(detalle.getEstado())
            .notaFinal(detalle.getNotaFinal())
            .build();
    }
    
        // MÉTODO DE VALIDACIÓN DE HORARIOS
    private void validarHorariosNoSolapados(UUID estudianteId, List<UUID> nuevasSeccionesIds) {
        if (nuevasSeccionesIds == null || nuevasSeccionesIds.isEmpty()) {
            return;
        }
        
        // Obtener horarios de las nuevas secciones
        Map<UUID, List<Horario>> horariosNuevasSecciones = new HashMap<>();
        for (UUID seccionId : nuevasSeccionesIds) {
            List<Horario> horarios = horarioRepository.findBySeccionId(seccionId);
            horariosNuevasSecciones.put(seccionId, horarios);
        }
        
        // Obtener horarios actuales del estudiante
        List<Horario> horariosActuales = horarioRepository.findHorariosByEstudianteId(estudianteId);
        
        // Validar conflictos entre nuevas secciones
        validarConflictosEntreNuevasSecciones(horariosNuevasSecciones);
        
        // Validar conflictos con horarios actuales
        validarConflictosConHorariosActuales(horariosNuevasSecciones, horariosActuales);
    }
    
    private void validarConflictosEntreNuevasSecciones(Map<UUID, List<Horario>> horariosNuevasSecciones) {
        List<Horario> todosHorariosNuevos = new ArrayList<>();
        horariosNuevasSecciones.values().forEach(todosHorariosNuevos::addAll);
        
        for (int i = 0; i < todosHorariosNuevos.size(); i++) {
            for (int j = i + 1; j < todosHorariosNuevos.size(); j++) {
                Horario h1 = todosHorariosNuevos.get(i);
                Horario h2 = todosHorariosNuevos.get(j);
                
                if (haySolapamiento(h1, h2)) {
                    throw new RuntimeException(
                        "Conflicto entre nuevas secciones: " +
                        h1.getSeccion().getCodigoSeccion() + " y " +
                        h2.getSeccion().getCodigoSeccion() + " - " +
                        h1.getDiaSemana() + " " + h1.getHoraInicio() + "-" + h1.getHoraFin()
                    );
                }
            }
        }
    }
    
    private void validarConflictosConHorariosActuales(
            Map<UUID, List<Horario>> horariosNuevasSecciones, 
            List<Horario> horariosActuales) {
        
        for (Map.Entry<UUID, List<Horario>> entry : horariosNuevasSecciones.entrySet()) {
            for (Horario nuevoHorario : entry.getValue()) {
                for (Horario horarioActual : horariosActuales) {
                    if (haySolapamiento(nuevoHorario, horarioActual)) {
                        throw new RuntimeException(
                            "Conflicto de horario: " +
                            nuevoHorario.getSeccion().getCodigoSeccion() + " (" +
                            nuevoHorario.getDiaSemana() + " " + 
                            nuevoHorario.getHoraInicio() + "-" + nuevoHorario.getHoraFin() +
                            ") se solapa con " +
                            horarioActual.getSeccion().getCodigoSeccion() + " (" +
                            horarioActual.getDiaSemana() + " " +
                            horarioActual.getHoraInicio() + "-" + horarioActual.getHoraFin() + ")"
                        );
                    }
                }
            }
        }
    }
    
    // Método para detectar solapamiento
    public boolean haySolapamiento(Horario h1, Horario h2) {
        // Mismo día
        if (!h1.getDiaSemana().equalsIgnoreCase(h2.getDiaSemana())) {
            return false;
        }
        
        // Verificar si se solapan
        // Caso 1: h1 comienza durante h2
        boolean h1DuranteH2 = 
            (h1.getHoraInicio().isAfter(h2.getHoraInicio()) || h1.getHoraInicio().equals(h2.getHoraInicio())) &&
            h1.getHoraInicio().isBefore(h2.getHoraFin());
        
        // Caso 2: h2 comienza durante h1
        boolean h2DuranteH1 = 
            (h2.getHoraInicio().isAfter(h1.getHoraInicio()) || h2.getHoraInicio().equals(h1.getHoraInicio())) &&
            h2.getHoraInicio().isBefore(h1.getHoraFin());
        
        // Caso 3: h1 contiene completamente a h2
        boolean h1ContieneH2 = 
            h1.getHoraInicio().isBefore(h2.getHoraInicio()) &&
            h1.getHoraFin().isAfter(h2.getHoraFin());
        
        // Caso 4: h2 contiene completamente a h1
        boolean h2ContieneH1 = 
            h2.getHoraInicio().isBefore(h1.getHoraInicio()) &&
            h2.getHoraFin().isAfter(h1.getHoraFin());
        
        return h1DuranteH2 || h2DuranteH1 || h1ContieneH2 || h2ContieneH1;
    }
    
    private void validarCapacidadSeccion(Seccion seccion) {
        Long inscritos = detalleMatriculaRepository.countBySeccionIdAndEstadoIn(
            seccion.getId(),
            Arrays.asList("INSCRITO", "EN_CURSO")
        );
        
        if (inscritos >= seccion.getCapacidad()) {
            throw new RuntimeException(
                "La sección " + seccion.getCodigoSeccion() + 
                " ha alcanzado su capacidad máxima (" + seccion.getCapacidad() + ")"
            );
        }
    }
    
    private Matricula crearMatriculaConDetalles(
            Estudiante estudiante, 
            String periodoAcademico, 
            List<Seccion> secciones) {
                Matricula matricula = Matricula.builder()
            .estudiante(estudiante)
            .periodoAcademico(periodoAcademico)
            .fechaMatricula(LocalDate.now())
            .estado("ACTIVA")
            .build();
        
        // Crear detalles de matrícula
        List<DetalleMatricula> detalles = secciones.stream()
            .map(seccion -> DetalleMatricula.builder()
                .matricula(matricula)
                .seccion(seccion)
                .estado("INSCRITO")
                .build())
            .collect(Collectors.toList());
        
        matricula.setDetalles(detalles);
        return matriculaPeriodoRepository.save(matricula);
    }
    
    public long contarMatriculasActivasPorPeriodo(String periodoAcademico, String estado){
        return matriculaPeriodoRepository.countByPeriodoAcademicoAndEstado(periodoAcademico, "ACTIVA");
    }
}