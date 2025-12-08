package com.educatoon.backend.academico.model;

import com.educatoon.backend.usuarios.model.Estudiante;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "matriculas")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Matricula {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;
    
    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula;
    
    
    @Column(name = "periodo_academico", nullable = false, length = 10)
    private String periodoAcademico; // "2024-I", "2024-II"
    
    @Column(nullable = false, length = 20)
    private String estado = "ACTIVA";
    
    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleMatricula> detalles = new ArrayList<>();
}