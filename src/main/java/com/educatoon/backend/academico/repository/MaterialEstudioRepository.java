package com.educatoon.backend.academico.repository;

import com.educatoon.backend.academico.model.MaterialEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID; // Necesario si añades métodos de búsqueda por UUID

@Repository
public interface MaterialEstudioRepository extends JpaRepository<MaterialEstudio, Long> {

    // Método de consulta simple para obtener materiales por Curso ID (UUID)
    List<MaterialEstudio> findByCursoId(UUID cursoId);
}