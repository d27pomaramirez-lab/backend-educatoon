
package com.educatoon.backend.academico.repository;

import com.educatoon.backend.academico.model.Seccion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hecto
 */

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, UUID> {

}
