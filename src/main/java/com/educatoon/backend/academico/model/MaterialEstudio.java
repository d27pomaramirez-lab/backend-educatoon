package com.educatoon.backend.academico.model;

import com.educatoon.backend.usuarios.model.Usuario; 
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID; // Importado para cursoId

@Entity
@Table(name = "material_estudio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialEstudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private String rutaArchivo;

    @Column(nullable = false, length = 50)
    private String tipoMime;

    // *** CORRECCIÓN: Tipo UUID para el ID del Curso ***
    @Column(nullable = false)
    private UUID cursoId; 
    
    // Relación con el Docente/Usuario que subió el material
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_subida_id")
    private Usuario usuarioSubida;

    @Column(nullable = false)
    private LocalDateTime fechaSubida = LocalDateTime.now();
}