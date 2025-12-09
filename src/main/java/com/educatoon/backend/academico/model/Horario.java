
package com.educatoon.backend.academico.model;

/**
 *
 * @author Aldair
 */

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "horarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Horario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "seccion_id", nullable = false)
    private Seccion seccion;
    
    @Column(name = "dia_semana", length = 15, nullable = false)
    private String diaSemana; // LUNES, MARTES, etc.
    
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}