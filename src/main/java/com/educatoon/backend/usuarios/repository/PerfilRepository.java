
package com.educatoon.backend.usuarios.repository;

import com.educatoon.backend.usuarios.model.Perfil;
import com.educatoon.backend.usuarios.model.Usuario;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego
 */

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, UUID>{
    Optional<Perfil> findByUsuarioId(UUID usuarioId);
}
