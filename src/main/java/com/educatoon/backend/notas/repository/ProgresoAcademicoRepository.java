package com.educatoon.backend.notas.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.educatoon.backend.notas.model.ProgresoAcademico;

@Repository
public interface ProgresoAcademicoRepository extends JpaRepository<ProgresoAcademico, UUID> {
    
}
