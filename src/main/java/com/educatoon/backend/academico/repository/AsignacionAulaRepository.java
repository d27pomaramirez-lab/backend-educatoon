/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.educatoon.backend.academico.repository;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.academico.model.AsignacionAula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AsignacionAulaRepository extends JpaRepository<AsignacionAula, UUID> {
    
    Optional<AsignacionAula> findByEstudianteId(UUID estudianteId);
    boolean existsByEstudianteId(UUID estudianteId);
    
    @Query("SELECT a FROM AsignacionAula a " +
           "JOIN FETCH a.estudiante e " +
           "JOIN FETCH a.seccion s " +
           "JOIN FETCH a.prueba p " +
           "JOIN FETCH a.horario h " +
           "WHERE a.estado = :estado")
    List<AsignacionAula> findByEstado(@Param("estado") String estado);
}