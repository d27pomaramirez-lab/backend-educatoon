package com.educatoon.backend.usuarios.repository;

import com.educatoon.backend.usuarios.model.Asesoria;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego
 */

@Repository
public interface AsesoriaRepository extends JpaRepository<Asesoria, UUID>{
    @Query("SELECT a FROM Asesoria a "
            + "LEFT JOIN FETCH a.estudiante e "
            + "LEFT JOIN FETCH e.usuario u "  //traemos el usuario del estudiante
            + "LEFT JOIN FETCH u.perfil p " // traemos su perfil
            + "LEFT JOIN FETCH a.docente d "
            + "LEFT JOIN FETCH d.usuario du " //traemos el usuario del docente
            + "LEFT JOIN FETCH du.perfil dp") //traemos su perfil
    List<Asesoria> findAllCompleto();
    
    List<Asesoria> findByEstudianteId(UUID estudianteId);
    List<Asesoria> findByDocenteId(UUID docenteId);
}
