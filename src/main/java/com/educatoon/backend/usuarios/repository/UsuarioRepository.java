
package com.educatoon.backend.usuarios.repository;

import com.educatoon.backend.usuarios.model.Usuario;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego
 */

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID>{
    Optional<Usuario> findByEmail(String email);    
    List<Usuario> findByEnabled(boolean enabled);
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.perfil")
    List<Usuario> findAllAndFetchPerfil();
    
    @Query("SELECT u FROM Usuario u " +
           "LEFT JOIN FETCH u.perfil " +
           "WHERE u.id = :id")
    Optional<Usuario> findByIdAndFetchPerfil(@Param("id") UUID id);
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.perfil WHERE u.email = :email")
    Optional<Usuario> findByEmailAndFetchPerfil(@Param("email") String email);
}
