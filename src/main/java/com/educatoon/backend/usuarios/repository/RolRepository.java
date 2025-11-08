
package com.educatoon.backend.usuarios.repository;

import com.educatoon.backend.usuarios.model.Rol;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego
 */

@Repository
public interface RolRepository extends JpaRepository<Rol, UUID>{
    Optional<Rol> findByNombre(String nombre);
}
