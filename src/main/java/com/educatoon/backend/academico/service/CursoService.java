
package com.educatoon.backend.academico.service;

import com.educatoon.backend.academico.dto.ActualizarCursoRequest;
import com.educatoon.backend.academico.dto.CursoRequest;
import com.educatoon.backend.academico.dto.CursoResponse;
import com.educatoon.backend.academico.model.Curso;
import com.educatoon.backend.academico.repository.CursoRepository;
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
public class CursoService {
    @Autowired 
    private CursoRepository cursoRepository;
    
    private CursoResponse mapToResponse(Curso curso) {
        CursoResponse response = new CursoResponse();
        response.setId(curso.getId());
        response.setNombre(curso.getNombre());
        response.setDescripcion(curso.getDescripcion());
        response.setCiclo(curso.getCiclo());
        response.setEstado(curso.isEstado());
        return response;
    }
    
    @Transactional
    public CursoResponse crearCurso(CursoRequest request) {
        if (cursoRepository.findByNombreIgnoreCase(request.getNombre()).isPresent()) {
            throw new RuntimeException("Error: El curso con el nombre '" + request.getNombre() + "' ya existe.");
        }

        Curso nuevoCurso = new Curso();
        nuevoCurso.setNombre(request.getNombre());
        nuevoCurso.setDescripcion(request.getDescripcion());
        nuevoCurso.setCiclo(request.getCiclo());
        nuevoCurso.setEstado(true);
        
        Curso cursoGuardado = cursoRepository.save(nuevoCurso);
        return mapToResponse(cursoGuardado);
    }

    public List<CursoResponse> listarTodos() {
        return cursoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public CursoResponse obtenerPorId(UUID id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + id));
        return mapToResponse(curso);
    }

    @Transactional
    public CursoResponse actualizarCurso(UUID id, ActualizarCursoRequest request) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + id));

        if (!curso.getNombre().equalsIgnoreCase(request.getNombre())) {
             if (cursoRepository.findByNombreIgnoreCase(request.getNombre()).isPresent()) {
                throw new RuntimeException("Error: El curso con el nuevo nombre '" + request.getNombre() + "' ya existe.");
            }
        }
        
        curso.setNombre(request.getNombre());
        curso.setDescripcion(request.getDescripcion());
        curso.setCiclo(request.getCiclo());
        
        Curso updatedCurso = cursoRepository.save(curso);
        return mapToResponse(updatedCurso);
    }

    @Transactional
    public String cambiarEstado(UUID id, boolean nuevoEstado) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + id));
        
        curso.setEstado(nuevoEstado);
        cursoRepository.save(curso);
        
        String estadoMsg = nuevoEstado ? "activado" : "desactivado";
        return "Curso '"+ curso.getNombre() +"' ha sido " + estadoMsg + " exitosamente.";
    }
}
