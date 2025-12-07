package com.educatoon.backend.asesorias.service;

import com.educatoon.backend.academico.model.Curso;
import com.educatoon.backend.academico.repository.CursoRepository;
import com.educatoon.backend.asesorias.dto.ActualizarAsesoriaRequest;
import com.educatoon.backend.asesorias.dto.AsesoriaResponse;
import com.educatoon.backend.asesorias.dto.CrearAsesoriaRequest;
import com.educatoon.backend.asesorias.model.Asesoria;
import com.educatoon.backend.usuarios.model.Docente;
import com.educatoon.backend.usuarios.model.Estudiante;
import com.educatoon.backend.asesorias.repository.AsesoriaRepository;
import com.educatoon.backend.usuarios.repository.DocenteRepository;
import com.educatoon.backend.usuarios.repository.EstudianteRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Diego
 */

@Service
public class AsesoriaService {
    @Autowired private AsesoriaRepository asesoriaRepository;
    @Autowired private EstudianteRepository estudianteRepository;
    @Autowired private DocenteRepository docenteRepository;
    @Autowired private CursoRepository cursoRepository;
    
    public List<AsesoriaResponse> listarAsesorias(){
        List<Asesoria> lista = asesoriaRepository.findAllCompleto();
        
        return lista.stream()
                .map(this::convertirAAsesoriaResponse)
                .collect(Collectors.toList());
    }
    
    private AsesoriaResponse convertirAAsesoriaResponse(Asesoria asesoria){
        AsesoriaResponse response = new AsesoriaResponse();
        
        response.setId(asesoria.getId());
        response.setTema(asesoria.getTema());
        response.setFecha(asesoria.getFecha());
        response.setDuracionMinutos(asesoria.getDuracionMinutos());
        response.setModalidad(asesoria.getModalidad());
        response.setEstado(asesoria.getEstado());
        response.setAreasRefuerzo(asesoria.getAreasRefuerzo());
        response.setObservaciones(asesoria.getObservaciones());
        response.setAsistencia(asesoria.getAsistencia());

        if ("VIRTUAL".equals(asesoria.getModalidad())) {
            response.setUbicacion(asesoria.getEnlaceReunion());
        } else {
            response.setUbicacion(asesoria.getLugarPresencial());
        }

        if (asesoria.getEstudiante() != null && asesoria.getEstudiante().getUsuario() != null && asesoria.getEstudiante().getUsuario().getPerfil() != null) {
             response.setNombreEstudiante(
                 asesoria.getEstudiante().getUsuario().getPerfil().getNombres() + " " + 
                 asesoria.getEstudiante().getUsuario().getPerfil().getApellidos()
             );
        }

        if (asesoria.getDocente() != null && asesoria.getDocente().getUsuario() != null && asesoria.getDocente().getUsuario().getPerfil() != null) {
             response.setNombreDocente(
                 asesoria.getDocente().getUsuario().getPerfil().getNombres() + " " + 
                 asesoria.getDocente().getUsuario().getPerfil().getApellidos()
             );
        }
        
        if (asesoria.getCurso() != null) {
            response.setNombreCurso(asesoria.getCurso().getNombre());
        }
            return response;    
        }        
    
    @Transactional
    public Asesoria crearAsesoria(CrearAsesoriaRequest request){
        Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
                .orElseThrow(()-> new RuntimeException("Estudiante no encontrado con ID: " + request.getEstudianteId()));
        
        Docente docente = docenteRepository.findById(request.getDocenteId())
                .orElseThrow(()-> new RuntimeException("Docente no encontrado con ID: " + request.getDocenteId()));
        
        Curso curso = cursoRepository.findById(request.getCursoId())
            .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + request.getCursoId()));
        
        Asesoria nuevaAsesoria = Asesoria.builder()
                .estudiante(estudiante)
                .docente(docente)
                .curso(curso)
                .fecha(request.getFecha())
                .duracionMinutos(request.getDuracionMinutos())
                .modalidad(request.getModalidad())
                .enlaceReunion(request.getEnlaceReunion())
                .lugarPresencial(request.getLugarPresencial())
                .tema(request.getTema())
                .areasRefuerzo(request.getAreasRefuerzo())
                .estado("PROGRAMADA")
                .asistencia(null)
                .build();
        return asesoriaRepository.save(nuevaAsesoria);
    }
    
    @Transactional
    public Asesoria actualizarAsesoria(UUID id, ActualizarAsesoriaRequest request) {
        Asesoria asesoria = asesoriaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Asesoría no encontrada"));

        if (!"PROGRAMADA".equals(asesoria.getEstado())) {
            throw new RuntimeException("No se puede editar una asesoría que ya fue realizada o cancelada.");
        }

        asesoria.setFecha(request.getFecha());
        asesoria.setDuracionMinutos(request.getDuracionMinutos());
        asesoria.setModalidad(request.getModalidad());
        
        if ("VIRTUAL".equals(request.getModalidad())) {
            asesoria.setEnlaceReunion(request.getEnlaceReunion());
            asesoria.setLugarPresencial(null);
        } else {
            asesoria.setLugarPresencial(request.getLugarPresencial());
            asesoria.setEnlaceReunion(null);
        }

        asesoria.setTema(request.getTema());
        asesoria.setAreasRefuerzo(request.getAreasRefuerzo());

        return asesoriaRepository.save(asesoria);
    }
    
    @Transactional
    public void cancelarAsesoria(UUID id) {
        Asesoria asesoria = asesoriaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Asesoría no encontrada"));

        asesoria.setEstado("CANCELADA");
        asesoriaRepository.save(asesoria);
    }
    
    public List<AsesoriaResponse> listarAsesoriasPorDocente(UUID docenteId) {
        List<Asesoria> asesorias = asesoriaRepository.findByDocenteId(docenteId);
        return asesorias.stream().map(this::convertirAAsesoriaResponse).collect(Collectors.toList());
    }
    
    @Transactional
    public String cambiarEstadoPorDocente(UUID asesoriaId, String nuevoEstado) {
        Asesoria asesoria = asesoriaRepository.findById(asesoriaId)
                .orElseThrow(() -> new RuntimeException("Asesoría no encontrada con ID: " + asesoriaId));

        if (!asesoria.getEstado().equals("PROGRAMADA")) {
            throw new RuntimeException("Solo se pueden cambiar el estado de asesorías 'PROGRAMADA'. Estado actual: " + asesoria.getEstado());
        }

        if (!nuevoEstado.equals("REALIZADA") && !nuevoEstado.equals("CANCELADA")) {
             throw new RuntimeException("Estado no permitido. Solo se acepta 'REALIZADA' o 'CANCELADA'.");
        }

        asesoria.setEstado(nuevoEstado);
        asesoriaRepository.save(asesoria);

        return "El estado de la asesoría ha cambiado a " + nuevoEstado + " exitosamente.";
    }
    
    public List<AsesoriaResponse> listarAsesoriasPorEstudiante(UUID estudianteId) {
        List<Asesoria> asesorias = asesoriaRepository.findByEstudianteId(estudianteId);
        return asesorias.stream().map(this::convertirAAsesoriaResponse).collect(Collectors.toList());
    }
    
}
