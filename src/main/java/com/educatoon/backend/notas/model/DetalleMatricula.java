package com.educatoon.backend.notas.model;

import java.util.UUID;

import com.educatoon.backend.academico.model.Seccion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "detalle_matriculas")
public class DetalleMatricula {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "matricula_id")
    private Matricula matricula;

    @ManyToOne
    @JoinColumn(name = "seccion_id")
    private Seccion seccion; // Asumimos que esta sí la tienes o la creas igual

    @OneToOne(mappedBy = "detalleMatricula")
    private ProgresoAcademico progresoAcademico;
}
