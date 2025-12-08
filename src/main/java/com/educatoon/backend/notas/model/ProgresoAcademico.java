package com.educatoon.backend.notas.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.educatoon.backend.academico.model.DetalleMatricula; 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
@Table(name = "progreso_academico")
public class ProgresoAcademico {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "detalle_matricula_id", nullable = false, unique = true)
    private DetalleMatricula detalleMatricula;

    @Column(name = "nota_parcial")
    private double notaParcial;

    @Column(name = "nota_final")
    private Double notaFinal; 

    @Column(name = "avance_porcentaje")
    private double avancePorcentaje;

    @Column(name = "promedio_simulacros")
    private double promedioSimulacros;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "simulacros_detalle", columnDefinition = "jsonb")
    private String simulacrosDetalle; // Guardamos el JSON como String
    
    @Column(name = "observaciones")
    private String observaciones;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
