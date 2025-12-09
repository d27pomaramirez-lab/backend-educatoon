/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.educatoon.backend.academico.repository;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.academico.model.DetalleMatricula;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DetalleMatriculaRepository extends JpaRepository<DetalleMatricula, UUID> {
    
    // Buscar detalles por matrícula
    List<DetalleMatricula> findByMatriculaId(UUID matriculaId);
    
    // Buscar detalles por sección
    List<DetalleMatricula> findBySeccionId(UUID seccionId);
    
    // Buscar detalles por estudiante (a través de la matrícula)
    @Query("SELECT dm FROM DetalleMatricula dm " +
           "JOIN dm.matricula m " +
           "WHERE m.estudiante.id = :estudianteId")
    List<DetalleMatricula> findByEstudianteId(@Param("estudianteId") UUID estudianteId);
    
    // Verificar si un estudiante ya está inscrito en una sección
    @Query("SELECT COUNT(dm) > 0 FROM DetalleMatricula dm " +
           "JOIN dm.matricula m " +
           "WHERE m.estudiante.id = :estudianteId " +
           "AND dm.seccion.id = :seccionId " +
           "AND dm.estado IN :estados")
    boolean existsByEstudianteAndSeccion(
        @Param("estudianteId") UUID estudianteId,
        @Param("seccionId") UUID seccionId,
        @Param("estados") List<String> estados
    );
    
    // Contar estudiantes inscritos en una sección
    Long countBySeccionIdAndEstadoIn(UUID seccionId, List<String> estados);
    
    // Buscar detalle específico por matrícula y sección
    Optional<DetalleMatricula> findByMatriculaIdAndSeccionId(UUID matriculaId, UUID seccionId);

    // Busca donde se encuentra matriculado un estudiante
    @EntityGraph(attributePaths = {"seccion", "seccion.curso", "progresoAcademico", "matricula"})
    List<DetalleMatricula> findByMatricula_Estudiante_IdAndMatricula_Estado(UUID estudianteId, String estado);

    // Busca estudiantes inscritos en una sección trayendo sus datos y progreso
    @EntityGraph(attributePaths = {
        "matricula.estudiante.usuario.perfil", 
        "progresoAcademico"
    })
    List<DetalleMatricula> findBySeccionIdAndEstado(UUID seccionId, String estado);

}