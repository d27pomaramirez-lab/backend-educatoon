package com.educatoon.backend.academico.model;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.usuarios.model.Estudiante;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "asignaciones_aulas")
@Data
public class AsignacionAula {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @OneToOne
    @JoinColumn(name = "estudiante_id", nullable = false, unique = true)
    private Estudiante estudiante;
    
    @ManyToOne
    @JoinColumn(name = "seccion_id", nullable = false)
    private Seccion seccion;
    
    @ManyToOne
    @JoinColumn(name = "prueba_id", nullable = false)
    private PruebaEntrada prueba;
    
    @ManyToOne
    @JoinColumn(name = "horario_id", nullable = false)
    private Horario horario;
    
    @Column(name = "razon_asignacion")
    private String razonAsignacion;
    
    @Column(length = 20)
    private String estado = "ASIGNADO";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}