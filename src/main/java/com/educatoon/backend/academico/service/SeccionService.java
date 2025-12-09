package com.educatoon.backend.academico.service;

import com.educatoon.backend.academico.dto.EstudianteDTO;
import com.educatoon.backend.academico.dto.EstudianteResumenDTO;
import com.educatoon.backend.academico.dto.SeccionConHorariosDTO;
import com.educatoon.backend.usuarios.dto.DocenteResponse;
import com.educatoon.backend.academico.dto.SeccionRequest;
import com.educatoon.backend.academico.dto.SeccionResponse;
import com.educatoon.backend.academico.model.Curso;
import com.educatoon.backend.academico.model.DetalleMatricula;
import com.educatoon.backend.academico.model.Horario;
import com.educatoon.backend.usuarios.model.Docente;
import com.educatoon.backend.usuarios.model.Estudiante;
import com.educatoon.backend.usuarios.model.Perfil;
import com.educatoon.backend.academico.model.Seccion;
import com.educatoon.backend.usuarios.model.Usuario;
import com.educatoon.backend.academico.repository.CursoRepository;
import com.educatoon.backend.academico.repository.DetalleMatriculaRepository;
import com.educatoon.backend.academico.repository.HorarioRepository;
import com.educatoon.backend.usuarios.repository.DocenteRepository;
import com.educatoon.backend.academico.repository.SeccionRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author hecto
 */

@Service
public class SeccionService {
    @Autowired DocenteRepository docenteRepository;
    @Autowired SeccionRepository seccionRepository;
    @Autowired CursoRepository cursoRepository;
    
    @Autowired DetalleMatriculaRepository detalleMatriculaRepository;
    @Autowired HorarioRepository horarioRepository;
    
    MatriculaService ms;
    
    public Seccion registrarSeccion(SeccionRequest request) {
        
        Curso curso = cursoRepository.findByNombreIgnoreCase(request.getCurso()).
                orElseThrow(() -> new RuntimeException("Curso no encontrado con nombre: " + request.getCurso()));
        
        Docente docente = null;
        if (request.getDocente() != null && !request.getDocente().isEmpty()) {
            docente = findDocenteByNombreCompleto(request.getDocente());
        }

        Seccion nuevaSeccion = new Seccion();
        
        nuevaSeccion.setCapacidad(request.getCapacidad());
        nuevaSeccion.setAula(request.getAula());
        
        String codigoSeccion =  generarCodigoParaSeccion(curso);

        nuevaSeccion.setCodigoSeccion(codigoSeccion);
        nuevaSeccion.setDocente(docente);
        nuevaSeccion.setCurso(curso);
        
        
        Seccion seccionGuardada = seccionRepository.save(nuevaSeccion);
        
        return seccionGuardada;
    }
    
    @Transactional
    public List<SeccionResponse> listarSecciones() {
        List<Seccion> secciones = seccionRepository.findAll();
        
        return secciones.stream()
            .map(this::convertirASeccionResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public SeccionResponse actualizarSeccion(UUID seccionId, SeccionRequest request) {
        Seccion seccionExistente = seccionRepository.findById(seccionId)
                .orElseThrow(() -> new RuntimeException("Sección no encontrada con id: " + seccionId));

        // Actualizar los campos de la sección
        seccionExistente.setCapacidad(request.getCapacidad());
        seccionExistente.setAula(request.getAula());

        if (request.getCurso() != null && !request.getCurso().isEmpty()) {
            Curso curso = cursoRepository.findByNombreIgnoreCase(request.getCurso())
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado con nombre: " + request.getCurso()));
            seccionExistente.setCurso(curso);
        }

        if (request.getDocente() != null && !request.getDocente().isEmpty()) {
            Docente docente = findDocenteByNombreCompleto(request.getDocente());
            seccionExistente.setDocente(docente);
        }

        // No es necesario llamar a save() explícitamente debido a @Transactional
        return convertirASeccionResponse(seccionExistente);
    }

    @Transactional
    public SeccionResponse eliminarSeccion(UUID seccionId){
        Seccion seccion = seccionRepository.findById(seccionId)
                .orElseThrow(() -> new RuntimeException("Sección no encontrada con id: " + seccionId));
        
        SeccionResponse response = convertirASeccionResponse(seccion);

        seccionRepository.delete(seccion);
        
        return response;
    }

    @Transactional
    public List<DocenteResponse> listarDocentes() {
        return docenteRepository.findAll().stream()
                .map(docente -> new DocenteResponse(
                    docente.getId(), 
                    getNombreCompletoFromDocente(docente)
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<EstudianteResumenDTO> listarEstudiantesPorSeccion(UUID seccionId) {
        // Buscamos estudiantes con estado INSCRITO o EN_CURSO
        List<DetalleMatricula> detalles = detalleMatriculaRepository.findBySeccionIdAndEstadoIn(
            seccionId, 
            Arrays.asList("INSCRITO", "EN_CURSO")
        );
        
        return detalles.stream()
            .map(this::convertirAEstudianteResumenDTO)
            .collect(Collectors.toList());
    }

    private EstudianteResumenDTO convertirAEstudianteResumenDTO(DetalleMatricula detalle) {
        Estudiante estudiante = detalle.getMatricula().getEstudiante();
        Perfil perfil = estudiante.getUsuario().getPerfil();
        
        return EstudianteResumenDTO.builder()
            .id(estudiante.getId())
            .nombreCompleto(perfil.getApellidos() + ", " + perfil.getNombres())
            .codigoEstudiante(estudiante.getCodigoEstudiante())
            .fechaMatricula(java.sql.Date.valueOf(detalle.getMatricula().getFechaMatricula()))
            .matriculadoActual(true)
            .build();
    }

    private SeccionResponse convertirASeccionResponse(Seccion seccion) {
        SeccionResponse response = new SeccionResponse();
        response.setId(seccion.getId());
        response.setCodigoSeccion(seccion.getCodigoSeccion());
        response.setCapacidad(seccion.getCapacidad());
        response.setAula(seccion.getAula());
        
        if (seccion.getCurso() != null) {
            response.setCurso(seccion.getCurso().getNombre());
        }
        
        response.setDocente(getNombreCompletoFromDocente(seccion.getDocente()));
        
        return response;
    }
    
    private Docente findDocenteByNombreCompleto(String nombreCompleto) {
        return docenteRepository.findAll().stream()
            .filter(d -> {
                Usuario usuario = d.getUsuario();
                if (usuario != null && usuario.getPerfil() != null) {
                    Perfil perfil = usuario.getPerfil();
                    String nombre = perfil.getNombres() + " " + perfil.getApellidos();
                    return nombre.equalsIgnoreCase(nombreCompleto);
                }
                return false;
            })
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Docente no encontrado con nombre: " + nombreCompleto));
    }

    private Docente findDocenteByIdString(String docenteIdString) {
        if (docenteIdString == null || docenteIdString.isEmpty()) {
            return null;
        }
        try {
            UUID docenteId = UUID.fromString(docenteIdString);
            return docenteRepository.findById(docenteId)
                    .orElseThrow(() -> new RuntimeException("Docente no encontrado con id: " + docenteIdString));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("El ID del docente proporcionado no es un UUID válido: " + docenteIdString);
        }
    }
    
    /*private SeccionRequest convertirASeccionRequest(Seccion seccion) {
        SeccionRequest res = new SeccionRequest();

        res.setAula(seccion.getAula());
        res.setCapacidad(seccion.getCapacidad());
        
        if (seccion.getCurso() != null) {
            Curso curso = seccion.getCurso();
            res.setCurso(curso.getNombre());
        }
        
        res.setDocente(getNombreCompletoFromDocente(seccion.getDocente()));

        return res;
    }*/

    private String getNombreCompletoFromDocente(Docente docente) {
        if (docente == null) {
            return "Sin asignar";
        }
        Usuario usuario = docente.getUsuario();
        if (usuario != null && usuario.getPerfil() != null) {
            Perfil perfil = usuario.getPerfil();
            return perfil.getNombres() + " " + perfil.getApellidos();
        } else {
            return "Docente sin nombre";
        }
    }
    
    private String generarCodigoParaSeccion(Curso curso){        
        // Obtener las 3 primeras letras del nombre del curso
        String nombre = curso.getNombre();
        String prefijo = (nombre.length() > 3 ? nombre.substring(0, 3) : nombre).toUpperCase();
        
        // Contar cuántas secciones existen de ESTE curso específico
        long totalSeccionesDelCurso = seccionRepository.countByCurso(curso);
        
        long nuevoNumero = totalSeccionesDelCurso + 1;

        // Formato: PRE-001 (Prefijo - Número con 3 dígitos)
        return String.format("%s-%03d", prefijo, nuevoNumero);
    }

    // Opción A: Búsqueda General (busca el texto en ambos campos)
    @Transactional
    public List<SeccionResponse> buscarGeneral(String criterio) {
        List<Seccion> resultados = seccionRepository
            .findByCodigoSeccionContainingIgnoreCaseOrCurso_NombreContainingIgnoreCase(criterio, criterio);
            
        return resultados.stream()
            .map(this::convertirASeccionResponse)
            .collect(Collectors.toList());
    }

    // Opción B: Buscar solo por Código
    @Transactional
    public List<SeccionResponse> buscarPorCodigo(String codigo) {
        return seccionRepository.findByCodigoSeccionContainingIgnoreCase(codigo).stream()
            .map(this::convertirASeccionResponse)
            .collect(Collectors.toList());
    }

    // Opción C: Buscar solo por Curso
    @Transactional
    public List<SeccionResponse> buscarPorCurso(String nombreCurso) {
        return seccionRepository.findByCurso_NombreContainingIgnoreCase(nombreCurso).stream()
            .map(this::convertirASeccionResponse)
            .collect(Collectors.toList());
    }

    public List<SeccionResponse> buscarPorCodigoYCurso(String codigo, String curso) {
        return seccionRepository.findByCodigoSeccionContainingIgnoreCaseAndCurso_NombreContainingIgnoreCase(codigo, curso)
            .stream().map(this::convertirASeccionResponse).collect(Collectors.toList());
    }
    
    public List<SeccionConHorariosDTO> obtenerSeccionesConHorarios() {
        return seccionRepository.findAll().stream()
            .map(this::convertirSeccionConHorariosDTO)
            .collect(Collectors.toList());
    }
    
    public List<SeccionConHorariosDTO> obtenerSeccionesDisponiblesParaEstudiante(UUID estudianteId) {
        // Obtener todas las secciones
        List<Seccion> todasSecciones = seccionRepository.findAll();
        List<SeccionConHorariosDTO> disponibles = new ArrayList<>();
        
        // Obtener secciones ya inscritas por el estudiante
        List<Seccion> seccionesInscritas = detalleMatriculaRepository
            .findByEstudianteId(estudianteId).stream()
            .filter(dm -> "INSCRITO".equals(dm.getEstado()) || 
                    "EN_CURSO".equals(dm.getEstado()))
            .map(DetalleMatricula::getSeccion)
            .collect(Collectors.toList());
        
        // Obtener horarios actuales del estudiante
        List<Horario> horariosEstudiante = Optional.ofNullable(
            horarioRepository.findHorariosByEstudianteId(estudianteId)
        ).orElse(new ArrayList<>());
        
        for (Seccion seccion : todasSecciones) {
            // Validar capacidad
            Long inscritos = detalleMatriculaRepository.countBySeccionIdAndEstadoIn(
                seccion.getId(),
                Arrays.asList("INSCRITO", "EN_CURSO")
            );
            
            if (inscritos >= seccion.getCapacidad()) {
                continue; // Sección llena
            }
            
            // Validar si ya está inscrito
            if (seccionesInscritas.contains(seccion)) {
                continue;
            }
            
            // Validar horarios
            List<Horario> horariosSeccion = horarioRepository.findBySeccionId(seccion.getId());
            boolean tieneConflicto = false;
            
            for (Horario horarioSeccion : horariosSeccion) {
                for (Horario horarioEstudiante : horariosEstudiante) {
                    if (ms.haySolapamiento(horarioSeccion, horarioEstudiante)) {
                        tieneConflicto = true;
                        break;
                    }
                }
                if (tieneConflicto) break;
            }
            
            if (!tieneConflicto) {
                disponibles.add(convertirSeccionConHorariosDTO(seccion));
            }
        }
        
        return disponibles;
    }
    
    private SeccionConHorariosDTO convertirSeccionConHorariosDTO(Seccion seccion) {
        List<Horario> horarios = horarioRepository.findBySeccionId(seccion.getId());
        Long inscritos = detalleMatriculaRepository.countBySeccionIdAndEstadoIn(
            seccion.getId(),
            Arrays.asList("INSCRITO", "EN_CURSO")
        );
        
        List<SeccionConHorariosDTO.HorarioDTO> horariosDTO = horarios.stream()
            .map(h -> SeccionConHorariosDTO.HorarioDTO.builder()
                .diaSemana(h.getDiaSemana())
                .horaInicio(h.getHoraInicio())
                .horaFin(h.getHoraFin())
                .horarioFormateado(h.getDiaSemana() + " " + 
                    h.getHoraInicio().toString() + "-" + h.getHoraFin().toString())
                .build())
            .collect(Collectors.toList());
        
        String nombreDocente = "No asignado";
        if (seccion.getDocente() != null && seccion.getDocente().getUsuario() != null &&
            seccion.getDocente().getUsuario().getPerfil() != null) {
            nombreDocente = seccion.getDocente().getUsuario().getPerfil().getNombres() + " " +
                           seccion.getDocente().getUsuario().getPerfil().getApellidos();
        }
        
        return SeccionConHorariosDTO.builder()
            .id(seccion.getId())
            .codigoSeccion(seccion.getCodigoSeccion())
            .cursoNombre(seccion.getCurso() != null ? seccion.getCurso().getNombre() : "Sin curso")
            .aula(seccion.getAula())
            .docenteNombre(nombreDocente)
            .capacidad(seccion.getCapacidad())
            .inscritos(inscritos.intValue())
            .cuposDisponibles(seccion.getCapacidad() - inscritos.intValue())
            .horarios(horariosDTO)
            .build();
    }
}
