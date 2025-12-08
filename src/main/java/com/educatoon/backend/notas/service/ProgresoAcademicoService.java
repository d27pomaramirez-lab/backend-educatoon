package com.educatoon.backend.notas.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.educatoon.backend.notas.dto.ProgresoResumenDTO;
import com.educatoon.backend.notas.model.DetalleMatricula;
import com.educatoon.backend.notas.model.ProgresoAcademico;
import com.educatoon.backend.notas.repository.DetalleMatriculaRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgresoAcademicoService {
    @Autowired
    private DetalleMatriculaRepository detalleRepo;

    @Transactional(readOnly = true)
    public List<ProgresoResumenDTO> obtenerProgresoPorEstudiante(UUID estudianteId) {
        // 1. Obtener la data de la BD usando el método "mágico" de Spring Data
        List<DetalleMatricula> detalles = detalleRepo.findByMatricula_EstudianteIdAndMatricula_Estado(estudianteId, "ACTIVA");

        // 2. Flujo Alternativo: Si está vacío, retornamos lista vacía
        if (detalles.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. Mapear a DTO
        return detalles.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    private ProgresoResumenDTO convertirADTO(DetalleMatricula detalle) {
        ProgresoResumenDTO dto = new ProgresoResumenDTO();
        
        // Datos del Curso y Sección
        if (detalle.getSeccion() != null) {
            dto.setCodigoSeccion(detalle.getSeccion().getCodigoSeccion());
            if (detalle.getSeccion().getCurso() != null) {
                dto.setNombreCurso(detalle.getSeccion().getCurso().getNombre());
            }
        }

        // Datos del Progreso (Manejo de Nulos por el LEFT JOIN implícito)
        if (detalle.getProgresoAcademico() != null) {
            ProgresoAcademico pa = detalle.getProgresoAcademico();
            
            dto.setNotaParcial(pa.getNotaParcial());
            dto.setAvance(pa.getAvancePorcentaje());
            dto.setObservaciones(pa.getObservaciones());
            dto.setUltimaActualizacion(pa.getUpdatedAt());
            
            // Lógica de negocio: Calcular estado
            if (pa.getNotaParcial() < 10.5) {
                dto.setEstado("EN RIESGO");
            } else {
                dto.setEstado("SATISFACTORIO");
            }
        } else {
            // Valores por defecto si no hay progreso registrado
            dto.setNotaParcial(0.0);
            dto.setAvance(0.0);
            dto.setEstado("SIN CALIFICAR");
            dto.setObservaciones("El docente aún no ha registrado notas.");
            dto.setUltimaActualizacion(null);
        }
        
        return dto;
    }

}
