package com.educatoon.backend.usuarios.service;

import com.educatoon.backend.usuarios.dto.AsesoriaResponse;
import com.educatoon.backend.usuarios.dto.CrearAsesoriaRequest;
import com.educatoon.backend.usuarios.model.Asesoria;
import com.educatoon.backend.usuarios.model.Docente;
import com.educatoon.backend.usuarios.model.Estudiante;
import com.educatoon.backend.usuarios.repository.AsesoriaRepository;
import com.educatoon.backend.usuarios.repository.DocenteRepository;
import com.educatoon.backend.usuarios.repository.EstudianteRepository;
import jakarta.transaction.Transactional;
import java.util.List;
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

        return response;    
    }        
    
    @Transactional
    public Asesoria crearAsesoria(CrearAsesoriaRequest request){
        Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
                .orElseThrow(()-> new RuntimeException("Estudiante no encontrado con ID: " + request.getEstudianteId()));
        
        Docente docente = docenteRepository.findById(request.getDocenteId())
                .orElseThrow(()-> new RuntimeException("Docente no encontrado con ID: " + request.getDocenteId()));
        
        Asesoria nuevaAsesoria = Asesoria.builder()
                .estudiante(estudiante)
                .docente(docente)
                .cursoId(request.getCursoId())
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
    
    
}
