
package com.educatoon.backend.academico.repository;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.academico.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, UUID> {
    
    // Obtener horarios por sección
    List<Horario> findBySeccionId(UUID seccionId);
    
    List<Horario> findBySeccionIdIn(List<UUID> seccionIds);
    
    // Obtener horarios por estudiante (a través de sus matrículas)
    @Query("SELECT DISTINCT h FROM Horario h " +
           "JOIN h.seccion s " +
           "JOIN DetalleMatricula dm ON dm.seccion = s " +
           "JOIN dm.matricula m " +
           "WHERE m.estudiante.id = :estudianteId " +
           "AND dm.estado IN ('INSCRITO', 'EN_CURSO')")
    List<Horario> findHorariosByEstudianteId(@Param("estudianteId") UUID estudianteId);
    
    // Buscar conflictos de horario para un estudiante
    @Query("SELECT h FROM Horario h " +
           "WHERE h.seccion.id IN :seccionIds " +
           "AND EXISTS (" +
           "  SELECT h2 FROM Horario h2 " +
           "  JOIN DetalleMatricula dm ON dm.seccion = h2.seccion " +
           "  JOIN dm.matricula m " +
           "  WHERE m.estudiante.id = :estudianteId " +
           "  AND dm.estado IN ('INSCRITO', 'EN_CURSO') " +
           "  AND h2.diaSemana = h.diaSemana " +
           "  AND (" +
           "    (h2.horaInicio < h.horaFin AND h2.horaFin > h.horaInicio)" +
           "  )" +
           ")")
    List<Horario> findConflictosHorario(
        @Param("estudianteId") UUID estudianteId,
        @Param("seccionIds") List<UUID> seccionIds
    );
}