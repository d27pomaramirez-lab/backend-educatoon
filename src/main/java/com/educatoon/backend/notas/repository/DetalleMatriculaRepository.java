package com.educatoon.backend.notas.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.educatoon.backend.notas.model.DetalleMatricula;

@Repository
public interface DetalleMatriculaRepository extends JpaRepository <DetalleMatricula, UUID>{
    @EntityGraph(attributePaths = {"seccion", "seccion.curso", "progresoAcademico", "matricula"})
    List<DetalleMatricula> findByMatricula_EstudianteIdAndMatricula_Estado(UUID estudianteId, String estado);
}
