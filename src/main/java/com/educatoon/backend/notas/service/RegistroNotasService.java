package com.educatoon.backend.notas.service;

import com.educatoon.backend.academico.model.DetalleMatricula;
import com.educatoon.backend.academico.repository.DetalleMatriculaRepository;
import com.educatoon.backend.notas.dto.EstudianteActaDTO;
import com.educatoon.backend.notas.dto.NotaEstudianteInput;
import com.educatoon.backend.notas.dto.RegistroNotasRequest;
import com.educatoon.backend.notas.model.ProgresoAcademico;
import com.educatoon.backend.notas.repository.ProgresoAcademicoRepository;
import com.educatoon.backend.usuarios.model.Perfil;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroNotasService {

    @Autowired
    private DetalleMatriculaRepository detalleMatriculaRepository;

    @Autowired
    private ProgresoAcademicoRepository progresoAcademicoRepository;

    // 1. Obtener lista de estudiantes para el acta (GET)
    @Transactional(readOnly = true)
    public List<EstudianteActaDTO> obtenerActaPorSeccion(UUID seccionId) {
        List<DetalleMatricula> inscritos = detalleMatriculaRepository.findBySeccionIdAndEstado(seccionId, "INSCRITO");

        return inscritos.stream()
                .map(this::convertirAEstudianteActaDTO)
                .collect(Collectors.toList());
    }

    // Método auxiliar para convertir DetalleMatricula a DTO
    private EstudianteActaDTO convertirAEstudianteActaDTO(DetalleMatricula detalle) {
        Perfil perfil = detalle.getMatricula().getEstudiante().getUsuario().getPerfil();
        ProgresoAcademico progreso = detalle.getProgresoAcademico();

        // Valores por defecto
        double notaParcial = 0.0;
        double notaFinal = 0.0;
        double promedioSimulacros = 0.0;
        String observaciones = "Sin observaciones";
        String estado = "Pendiente";

        if (progreso != null) {
            notaParcial = progreso.getNotaParcial();
            notaFinal = progreso.getNotaFinal() != null ? progreso.getNotaFinal() : 0.0;
            promedioSimulacros = progreso.getPromedioSimulacros();
            observaciones = progreso.getObservaciones();
            
            estado = calcularEstado(notaParcial, notaFinal, promedioSimulacros);
        }

        return EstudianteActaDTO.builder()
                .detalleMatriculaId(detalle.getId())
                .codigoEstudiante(detalle.getMatricula().getEstudiante().getCodigoEstudiante())
                .nombreCompleto(perfil.getApellidos() + ", " + perfil.getNombres())
                .notaParcial(notaParcial)
                .notaFinal(notaFinal)
                .promedioSimulacros(promedioSimulacros)
                .observaciones(observaciones)
                .estado(estado)
                .build();
    }

    // Método auxiliar para calcular el estado académico
    private String calcularEstado(double parcial, double finalNota, double simulacros) {
        double promedio = (parcial + finalNota + simulacros) / 3;
        
        if (promedio >= 10.5) {
            return "Aprobado";
        } else if (promedio > 0) {
            return "Desaprobado";
        } else {
            return "Pendiente";
        }
    }


    // 2. Guardar notas (POST)
    @Transactional
    public void registrarNotas(RegistroNotasRequest request) {
        for (NotaEstudianteInput notaInput : request.getNotas()) {
            
            // Validar que las notas estén entre 0 y 20
            validarRangoNota(notaInput.getNotaParcial(), "Parcial");
            validarRangoNota(notaInput.getNotaFinal(), "Final");
            validarRangoNota(notaInput.getPromedioSimulacros(), "Promedio Simulacros");
           
            DetalleMatricula detalle = detalleMatriculaRepository.findById(notaInput.getDetalleMatriculaId())
                    .orElseThrow(() -> new RuntimeException("Detalle matrícula no encontrado: " + notaInput.getDetalleMatriculaId()));

            ProgresoAcademico progreso = detalle.getProgresoAcademico();

            if (progreso == null) {
                progreso = new ProgresoAcademico();
                progreso.setDetalleMatricula(detalle);
                // Inicializar otros campos si es necesario
            }

            progreso.setNotaParcial(notaInput.getNotaParcial());
            progreso.setNotaFinal(notaInput.getNotaFinal());
            progreso.setPromedioSimulacros(notaInput.getPromedioSimulacros()); 


            if (notaInput.getObservaciones() != null) {
                progreso.setObservaciones(notaInput.getObservaciones());
            }
            
            progresoAcademicoRepository.save(progreso);
        }
    }

      private void validarRangoNota(double nota, String nombreCampo) {
        if (nota < 0 || nota > 20) {
            throw new IllegalArgumentException("La nota '" + nombreCampo + "' debe estar entre 0 y 20. Valor recibido: " + nota);
        }
    }
}