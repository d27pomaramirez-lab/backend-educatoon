/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.educatoon.backend.academico.service;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.academico.dto.CrearHorarioRequest;
import com.educatoon.backend.academico.dto.HorariosDTO;
import com.educatoon.backend.academico.model.Horario;
import com.educatoon.backend.academico.model.Seccion;
import com.educatoon.backend.academico.repository.HorarioRepository;
import com.educatoon.backend.academico.repository.SeccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HorarioCrudService {
    
    private final HorarioRepository horarioRepository;
    private final SeccionRepository seccionRepository;
    
    public HorariosDTO crearHorario(CrearHorarioRequest request) {
        // Validar sección
        Seccion seccion = seccionRepository.findById(request.getSeccionId())
            .orElseThrow(() -> new RuntimeException("Sección no encontrada"));
        
        // Validar que no haya conflictos de horario en la misma sección
        validarConflictosHorario(seccion.getId(), request);
        
        Horario horario = Horario.builder()
            .seccion(seccion)
            .diaSemana(request.getDiaSemana().toUpperCase())
            .horaInicio(request.getHoraInicio())
            .horaFin(request.getHoraFin())
            .build();
        
        Horario guardado = horarioRepository.save(horario);
        return convertirADTO(guardado);
    }
    
    public HorariosDTO actualizarHorario(UUID id, CrearHorarioRequest request) {
        Horario horario = horarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        
        // Validar cambios
        if (!horario.getSeccion().getId().equals(request.getSeccionId())) {
            throw new RuntimeException("No se puede cambiar la sección de un horario existente");
        }
        
        // Validar conflictos (excluyendo este horario)
        validarConflictosHorario(horario.getSeccion().getId(), request, id);
        
        horario.setDiaSemana(request.getDiaSemana().toUpperCase());
        horario.setHoraInicio(request.getHoraInicio());
        horario.setHoraFin(request.getHoraFin());
        
        Horario actualizado = horarioRepository.save(horario);
        return convertirADTO(actualizado);
    }
    
    public void eliminarHorario(UUID id) {
        Horario horario = horarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        
        horarioRepository.delete(horario);
    }
    
    public List<HorariosDTO> getHorariosPorSeccion(UUID seccionId) {
        return horarioRepository.findBySeccionId(seccionId).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    private void validarConflictosHorario(UUID seccionId, CrearHorarioRequest request) {
        validarConflictosHorario(seccionId, request, null);
    }
    
    private void validarConflictosHorario(UUID seccionId, CrearHorarioRequest request, UUID horarioIdExcluir) {
        List<Horario> horariosExistentes = horarioRepository.findBySeccionId(seccionId);
        
        for (Horario existente : horariosExistentes) {
            // Excluir el horario que se está editando
            if (horarioIdExcluir != null && existente.getId().equals(horarioIdExcluir)) {
                continue;
            }
            
            // Mismo día
            if (existente.getDiaSemana().equalsIgnoreCase(request.getDiaSemana())) {
                // Verificar solapamiento
                boolean seSolapan = 
                    (request.getHoraInicio().isBefore(existente.getHoraFin()) && 
                     request.getHoraFin().isAfter(existente.getHoraInicio()));
                
                if (seSolapan) {
                    throw new RuntimeException(
                        "Conflicto de horario: " + existente.getDiaSemana() + " " +
                        existente.getHoraInicio() + "-" + existente.getHoraFin()
                    );
                }
            }
        }
    }
    
    private HorariosDTO convertirADTO(Horario horario) {
        return HorariosDTO.builder()
            .id(horario.getId())
            .seccionId(horario.getSeccion().getId())
            .seccionCodigo(horario.getSeccion().getCodigoSeccion())
            .cursoNombre(horario.getSeccion().getCurso() != null ? 
                        horario.getSeccion().getCurso().getNombre() : "Sin curso")
            .diaSemana(horario.getDiaSemana())
            .horaInicio(horario.getHoraInicio())
            .horaFin(horario.getHoraFin())
            .aula(horario.getSeccion().getAula())
            .build();
    }
}