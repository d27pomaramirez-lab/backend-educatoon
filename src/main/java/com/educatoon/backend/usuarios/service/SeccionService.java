/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.educatoon.backend.usuarios.service;

import com.educatoon.backend.usuarios.dto.SeccionRequest;
import com.educatoon.backend.usuarios.model.Curso;
import com.educatoon.backend.usuarios.model.Docente;
import com.educatoon.backend.usuarios.model.Seccion;
import com.educatoon.backend.usuarios.repository.CursoRepository;
import com.educatoon.backend.usuarios.repository.DocenteRepository;
import com.educatoon.backend.usuarios.repository.SeccionRepository;
import com.educatoon.backend.usuarios.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author hecto
 */

@Service
public class SeccionService {
    @Autowired DocenteRepository docenteRepository;
    @Autowired SeccionRepository seccionRepository;
    @Autowired CursoRepository cursoRepository;
    
    public Seccion registrarSeccion(SeccionRequest request) {
        
        Curso curso = cursoRepository.findByNombre(request.getCurso()).
                orElseThrow(() -> new RuntimeException("Curso no encontrado con nombre: " + request.getCurso()));
        
        //Docente docente = docenteRepository;
      
        
        Seccion nuevaSeccion = new Seccion();
        
        nuevaSeccion.setCapacidad(request.getCapacidad());
        nuevaSeccion.setAula(request.getAula());
        
        String codigoSeccion =  generarCodigoParaSeccion(nuevaSeccion, request);

        nuevaSeccion.setCodigoSeccion(codigoSeccion);
        
        //nuevaSeccion.setDocente
        nuevaSeccion.setCurso(curso);
        
        
        Seccion seccionGuardada = seccionRepository.save(nuevaSeccion);
        
        return seccionGuardada;
    }
    
    public List<SeccionRequest> listarSecciones() {
        List<Seccion> secciones = seccionRepository.findAll();
        
        return secciones.stream()
            .map(this::convertirASeccionRequest)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public Seccion actualizarSeccion(UUID seccionId, SeccionRequest request) {
        Seccion seccionExistente = seccionRepository.findById(seccionId)
                .orElseThrow(() -> new RuntimeException("Sección no encontrada con id: " + seccionId));

        // Actualizar los campos de la sección
        seccionExistente.setCapacidad(request.getCapacidad());
        seccionExistente.setAula(request.getAula());

        if (request.getCurso() != null && !request.getCurso().isEmpty()) {
            Curso curso = cursoRepository.findByNombre(request.getCurso())
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado con nombre: " + request.getCurso()));
            seccionExistente.setCurso(curso);
        }

        // No es necesario llamar a save() explícitamente debido a @Transactional
        return seccionExistente;
    }

    @Transactional
    public Seccion eliminarSeccion(UUID seccionId){
        Seccion seccion = seccionRepository.findById(seccionId)
                .orElseThrow(() -> new RuntimeException("Sección no encontrada con id: " + seccionId));
        seccionRepository.delete(seccion);
        return seccion;
    }
    
    private SeccionRequest convertirASeccionRequest(Seccion seccion) {
        SeccionRequest res = new SeccionRequest();

        res.setAula(seccion.getAula());
        res.setCapacidad(seccion.getCapacidad());
        
        if (seccion.getCurso() != null) {
            Curso curso = seccion.getCurso();
            res.setCurso(curso.getNombre());
        }
        
        res.setDocente("Aleatorio");
        
        return res;
    }
    
    private String generarCodigoParaSeccion(Seccion seccion, SeccionRequest request){        
        Random rand = new Random();
        
        int min = 5;
        int max = 15;
        int valorAleatorio = rand.nextInt(max - min + 1) + min;
        
        return "CU00" + valorAleatorio;
    }
}
