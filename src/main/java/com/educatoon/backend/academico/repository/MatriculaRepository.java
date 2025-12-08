package com.educatoon.backend.academico.repository;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.academico.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, UUID> {
    
    // Verificar si ya existe matrícula para un estudiante en un período
    boolean existsByEstudianteIdAndPeriodoAcademico(UUID estudianteId, String periodoAcademico);
    
    // Buscar matrículas por estudiante
    List<Matricula> findByEstudianteId(UUID estudianteId);
    
    // Buscar matrícula activa por estudiante y período
    Optional<Matricula> findByEstudianteIdAndPeriodoAcademico(UUID estudianteId, String periodoAcademico);
    
    // Buscar matrículas por período académico
    List<Matricula> findByPeriodoAcademico(String periodoAcademico);
    
    // Buscar matrículas por estado
    List<Matricula> findByEstado(String estado);
    
    // Contar matrículas activas por período
    Long countByPeriodoAcademicoAndEstado(String periodoAcademico, String estado);
    
    // Obtener todas con detalles usando JOIN FETCH (optimizado)
    @Query("SELECT DISTINCT mp FROM Matricula mp " +
           "LEFT JOIN FETCH mp.detalles d " +
           "LEFT JOIN FETCH d.seccion s " +
           "LEFT JOIN FETCH s.curso c " +
           "LEFT JOIN FETCH s.docente doc " +
           "LEFT JOIN FETCH mp.estudiante e " +
           "LEFT JOIN FETCH e.usuario u " +
           "LEFT JOIN FETCH u.perfil p " +
           "ORDER BY mp.fechaMatricula DESC")
    List<Matricula> findAllWithDetalles();
}