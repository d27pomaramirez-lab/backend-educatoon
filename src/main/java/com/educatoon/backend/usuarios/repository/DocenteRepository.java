
package com.educatoon.backend.usuarios.repository;

import com.educatoon.backend.usuarios.model.Docente;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego
 */

@Repository
public interface DocenteRepository extends JpaRepository<Docente, UUID>{
    
}
