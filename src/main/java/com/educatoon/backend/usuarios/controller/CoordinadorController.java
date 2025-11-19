package com.educatoon.backend.usuarios.controller;

import com.educatoon.backend.usuarios.dto.SeccionRequest;
import com.educatoon.backend.usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.educatoon.backend.usuarios.dto.UsuarioPendienteDTO;
import com.educatoon.backend.usuarios.model.Seccion;
import com.educatoon.backend.usuarios.service.SeccionService;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Diego
 */

@RestController
@RequestMapping("/api/coordinador")
public class CoordinadorController {
    
    @Autowired private UsuarioService usuarioService;
    @Autowired private SeccionService seccionService;
    
    @PostMapping("/validar-documentos/{id}")
    public ResponseEntity<?> validarDocumentosEstudiante(@PathVariable UUID id){
        try {
            usuarioService.validarDocumentos(id);
            return ResponseEntity.ok("Documentos del estudiante validados exitosamente.");                    
        }catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/pendientes")
    public List<UsuarioPendienteDTO> getUsuariosPendientes() {
        return usuarioService.getUsuariosPendientes();
    }
    
    @PostMapping("/registrar-seccion")
    public ResponseEntity<?> registrarSeccion(@RequestBody SeccionRequest request){
        try {
            Seccion nuevaSeccion = seccionService.registrarSeccion(request);
            return ResponseEntity.ok("Seccion " + nuevaSeccion.getCodigoSeccion() + " creada exitosamente.");                    
        }catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/listar-secciones")
    public List<SeccionRequest> ListarSecciones() {
        return seccionService.listarSecciones();
    }

    @PutMapping("/actualizar-seccion/{id}")
    public ResponseEntity<?> actualizarSeccion(@PathVariable UUID id, @RequestBody SeccionRequest request) {
        try {
            Seccion seccionActualizada = seccionService.actualizarSeccion(id, request);
            return ResponseEntity.ok(seccionActualizada);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/eliminar-seccion/{id}")
    public ResponseEntity<?> eliminarSeccion(@PathVariable UUID id) {
        try {
           Seccion seccionEliminada = seccionService.eliminarSeccion(id);
            return ResponseEntity.ok(seccionEliminada);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
