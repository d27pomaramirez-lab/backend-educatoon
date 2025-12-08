package com.educatoon.backend.academico.model;

/**
 *
 * @author Aldair
 */

import com.educatoon.backend.usuarios.model.Estudiante;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pruebas_entrada")
@Data
public class PruebaEntrada {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @OneToOne
    @JoinColumn(name = "estudiante_id", nullable = false, unique = true)
    private Estudiante estudiante;
    
    @Column(name = "fecha_rendicion", nullable = false)
    private LocalDate fechaRendicion;
    
    @Column(name = "puntaje_total")
    private Double puntajeTotal;
    
    @Column(name = "perfil_aprendizaje", length = 50)
    private String perfilAprendizaje;
    
    @Column(length = 20)
    private String estado = "EVALUADO";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (fechaRendicion == null) {
            fechaRendicion = LocalDate.now();
        }
    }
}