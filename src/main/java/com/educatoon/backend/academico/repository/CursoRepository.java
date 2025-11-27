
package com.educatoon.backend.academico.repository;

import com.educatoon.backend.academico.model.Curso;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hecto
 */

@Repository
public interface CursoRepository extends JpaRepository<Curso, UUID>{
    Optional<Curso> findByNombreIgnoreCase(String nombre);
}
