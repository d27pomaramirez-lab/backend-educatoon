package com.educatoon.backend.academico.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.educatoon.backend.notas.model.ProgresoAcademico;

/**
 *
 * @author Aldair
 */

@Entity
@Table(name = "detalle_matriculas")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleMatricula {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seccion_id", nullable = false)
    private Seccion seccion;
    
    @Column(nullable = false, length = 20)
    private String estado = "INSCRITO";
    
    @Column(name = "nota_final", precision = 4, scale = 2)
    private BigDecimal notaFinal;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @OneToOne(mappedBy = "detalleMatricula")
    private ProgresoAcademico progresoAcademico;
}

//INSCRITO, RETIRADO, APROBADO, DESAPROBADO, EN_CURSO