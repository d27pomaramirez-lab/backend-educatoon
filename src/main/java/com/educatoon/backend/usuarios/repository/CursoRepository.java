/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.educatoon.backend.usuarios.repository;

import com.educatoon.backend.usuarios.model.Curso;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hecto
 */
@Repository
public interface CursoRepository extends JpaRepository<Curso, UUID>{
    Optional<Curso> findByNombre(String nombre);
}
