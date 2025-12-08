package com.educatoon.backend.usuarios.repository;

import com.educatoon.backend.usuarios.model.Estudiante;
import java.awt.print.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 *
 * @author Diego
 */

// EstudianteRepository.java
@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, UUID> {
    Optional<Estudiante> findByCodigoEstudiante(String codigoEstudiante);
    
    // Método optimizado con JOIN FETCH
    @Query("SELECT e FROM Estudiante e " +
           "JOIN FETCH e.usuario u " +
           "JOIN FETCH u.perfil p " +
           "LEFT JOIN FETCH u.rol r " +
           "ORDER BY p.apellidos, p.nombres")
    List<Estudiante> findAllWithUsuarioAndPerfil();
    
    // Método optimizado para búsqueda por nombre
    @Query("SELECT e FROM Estudiante e " +
           "JOIN FETCH e.usuario u " +
           "JOIN FETCH u.perfil p " +
           "WHERE LOWER(CONCAT(p.nombres, ' ', p.apellidos)) LIKE LOWER(:nombre) " +
           "OR LOWER(p.nombres) LIKE LOWER(:nombre) " +
           "OR LOWER(p.apellidos) LIKE LOWER(:nombre)")
    List<Estudiante> buscarPorNombreCompleto(@Param("nombre") String nombre);
    
        @Query("SELECT e FROM Estudiante e " +
           "JOIN e.usuario u " +
           "JOIN u.perfil p " +
           "WHERE LOWER(p.nombres) LIKE LOWER(:termino) " +
           "OR LOWER(p.apellidos) LIKE LOWER(:termino) " +
           "OR LOWER(CONCAT(p.nombres, ' ', p.apellidos)) LIKE LOWER(:termino) " +
           "OR LOWER(e.codigoEstudiante) LIKE LOWER(:termino) " +
           "ORDER BY p.apellidos, p.nombres")
    List<Estudiante> busquedaRapida(@Param("termino") String termino);
    
    // Versión con límite
    @Query("SELECT e FROM Estudiante e " +
           "JOIN e.usuario u " +
           "JOIN u.perfil p " +
           "WHERE LOWER(p.nombres) LIKE LOWER(:termino) " +
           "OR LOWER(p.apellidos) LIKE LOWER(:termino) " +
           "OR LOWER(CONCAT(p.nombres, ' ', p.apellidos)) LIKE LOWER(:termino) " +
           "OR LOWER(e.codigoEstudiante) LIKE LOWER(:termino) " +
           "ORDER BY p.apellidos, p.nombres")
    List<Estudiante> busquedaRapida(@Param("termino") String termino, int pageable);
}