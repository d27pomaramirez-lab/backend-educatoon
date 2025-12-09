package com.educatoon.backend.notas.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.educatoon.backend.notas.dto.ProgresoResumenDTO;
import com.educatoon.backend.academico.model.DetalleMatricula;
import com.educatoon.backend.notas.model.ProgresoAcademico;
import com.educatoon.backend.usuarios.model.Docente;
import com.educatoon.backend.usuarios.model.Estudiante;
import com.educatoon.backend.usuarios.model.Perfil;
import com.educatoon.backend.usuarios.model.Usuario;
import com.educatoon.backend.usuarios.repository.EstudianteRepository;
import com.educatoon.backend.academico.repository.DetalleMatriculaRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgresoAcademicoService {
    @Autowired
    private DetalleMatriculaRepository detalleRepo;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Transactional(readOnly = true)
    public List<ProgresoResumenDTO> obtenerProgresoPorDni(String dni) {
        Estudiante estudiante = estudianteRepository.findByUsuario_Perfil_Dni(dni)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún estudiante con el DNI: " + dni));
        
        // Reutilizamos el método existente pasando el ID encontrado
        return obtenerProgresoPorEstudiante(estudiante.getId());
    }

    @Transactional(readOnly = true)
    public List<ProgresoResumenDTO> obtenerProgresoPorEstudiante(UUID estudianteId) {
        // Llamar al método con el nombre actualizado (Estudiante_Id en lugar de EstudianteId)
        List<DetalleMatricula> detalles = detalleRepo.findByMatricula_Estudiante_IdAndMatricula_Estado(estudianteId, "ACTIVA");

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

            dto.setNombreDocente(getNombreCompletoFromDocente(detalle.getSeccion().getDocente()));
        }

        // Datos del Progreso (Manejo de Nulos por el LEFT JOIN implícito)
        if (detalle.getProgresoAcademico() != null) {
            ProgresoAcademico pa = detalle.getProgresoAcademico();
            
            dto.setNotaParcial(pa.getNotaParcial());
            dto.setNotaFinal(pa.getNotaFinal()); // Mapear nota final
            dto.setPromedioSimulacros(pa.getPromedioSimulacros()); // Mapear promedio
            
            // CALCULO DEL AVANCE (0-100%) BASADO EN EL PROMEDIO (0-20)
            double notaFinalSafe = pa.getNotaFinal() != null ? pa.getNotaFinal() : 0.0;
            double promedio = (pa.getNotaParcial() + notaFinalSafe + pa.getPromedioSimulacros()) / 3;
            
            // Regla de tres: 20 es a 100%, Promedio es a X%
            double porcentajeAvance = (promedio / 20.0) * 100.0;
            dto.setAvance(Math.round(porcentajeAvance * 100.0) / 100.0);            

            dto.setObservaciones(pa.getObservaciones());
            dto.setUltimaActualizacion(pa.getUpdatedAt());
            
            // Lógica de estado actualizada
            if (pa.getNotaFinal() != null) {
                // Si ya hay nota final, el estado depende del PROMEDIO TOTAL de los 3 componentes
                // (La variable 'promedio' ya fue calculada unas líneas arriba)
                dto.setEstado(promedio >= 10.5 ? "APROBADO" : "DESAPROBADO");
            } else {
                // Si no hay nota final, evaluamos el desempeño parcial (Parcial + Simulacros)
                double promedioTemporal = (pa.getNotaParcial() + pa.getPromedioSimulacros()) / 2;
                
                if (promedioTemporal < 10.5) {
                    dto.setEstado("EN RIESGO");
                } else {
                    dto.setEstado("SATISFACTORIO");
                }
            }

        } else {
            // Valores por defecto
            dto.setNotaParcial(0.0);
            dto.setNotaFinal(0.0); // Sin nota final
            dto.setPromedioSimulacros(0.0);
            dto.setAvance(0.0);
            dto.setEstado("SIN CALIFICAR");
            dto.setObservaciones("El docente aún no ha registrado notas.");
            dto.setUltimaActualizacion(null);
        }
        
        return dto;
    }

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

}
