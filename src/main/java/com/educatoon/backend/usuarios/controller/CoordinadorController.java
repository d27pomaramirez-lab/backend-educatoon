package com.educatoon.backend.usuarios.controller;

import com.educatoon.backend.asesorias.dto.ActualizarAsesoriaRequest;
import com.educatoon.backend.asesorias.dto.AsesoriaResponse;
import com.educatoon.backend.asesorias.dto.CrearAsesoriaRequest;
import com.educatoon.backend.usuarios.dto.SeccionRequest;
import com.educatoon.backend.usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.educatoon.backend.usuarios.dto.UsuarioPendienteResponse;
import com.educatoon.backend.asesorias.model.Asesoria;
import com.educatoon.backend.asesorias.service.AsesoriaService;
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
    @Autowired private AsesoriaService asesoriaService;
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
    public List<UsuarioPendienteResponse> getUsuariosPendientes() {
        return usuarioService.getUsuariosPendientes();
    }
    
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioPendienteResponse>> listarUsuariosParaCoordinador() {
        return ResponseEntity.ok(usuarioService.getTodosLosUsuarios());
    }
    
    @GetMapping("/asesorias")
    public ResponseEntity<List<AsesoriaResponse>> listarAsesorias() {
        return ResponseEntity.ok(asesoriaService.listarAsesorias());
    }
    
    @PostMapping("/asesorias/crear")
    public ResponseEntity<?> crearAsesoria(@RequestBody CrearAsesoriaRequest request) {
        try {
            Asesoria asesoria = asesoriaService.crearAsesoria(request);
            return ResponseEntity.ok("Asesoría programada con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/asesorias/actualizar/{id}")
    public ResponseEntity<?> actualizarAsesoria(@PathVariable UUID id, @RequestBody ActualizarAsesoriaRequest request) {
        try {
            asesoriaService.actualizarAsesoria(id, request);
            return ResponseEntity.ok("Asesoría actualizada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/asesorias/cancelar/{id}")
    public ResponseEntity<?> cancelarAsesoria(@PathVariable UUID id) {
        try {
            asesoriaService.cancelarAsesoria(id);
            return ResponseEntity.ok("Asesoría cancelada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
