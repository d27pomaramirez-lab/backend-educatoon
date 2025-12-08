package com.educatoon.backend.academico.repository;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.academico.model.PruebaEntrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PruebaEntradaRepository extends JpaRepository<PruebaEntrada, UUID> {
    
    Optional<PruebaEntrada> findByEstudianteId(UUID estudianteId);
    boolean existsByEstudianteId(UUID estudianteId);
    
    @Query("SELECT p FROM PruebaEntrada p " +
           "JOIN FETCH p.estudiante e " +
           "JOIN FETCH e.usuario u " +
           "JOIN FETCH u.perfil " +
           "WHERE p.estado = :estado")
    List<PruebaEntrada> findByEstado(@Param("estado") String estado);
    
    List<PruebaEntrada> findByPerfilAprendizaje(String perfilAprendizaje);
}
