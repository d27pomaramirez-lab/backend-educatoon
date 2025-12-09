
package com.educatoon.backend.academico.repository;

import com.educatoon.backend.academico.model.Curso;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hecto
 */

@Repository
public interface CursoRepository extends JpaRepository<Curso, UUID>{
    Optional<Curso> findByNombreIgnoreCase(String nombre);
    
    
    @Query("SELECT DISTINCT c FROM Curso c JOIN Seccion s ON s.curso = c WHERE s.docente.usuario.id = :docenteId")
    List<Curso> findCursosByDocenteId(@Param("docenteId") UUID docenteId);
   

}
