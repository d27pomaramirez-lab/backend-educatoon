
package com.educatoon.backend.academico.repository;

import com.educatoon.backend.academico.model.Seccion;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hecto
 */

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, UUID> {
    List<Seccion> findByCodigoSeccionContainingIgnoreCase(String codigo);
    
    List<Seccion> findByCurso_NombreContainingIgnoreCase(String nombreCurso);

    List<Seccion> findByCodigoSeccionContainingIgnoreCaseOrCurso_NombreContainingIgnoreCase(String codigo, String nombreCurso);

    List<Seccion> findByCodigoSeccionContainingIgnoreCaseAndCurso_NombreContainingIgnoreCase(String codigo, String nombreCurso);
}
