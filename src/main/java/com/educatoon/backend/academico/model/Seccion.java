
package com.educatoon.backend.academico.model;

import com.educatoon.backend.usuarios.model.Docente;
import com.educatoon.backend.utils.AuditBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.Data;

/**
 *
 * @author hecto
 */

@Entity
@NoArgsConstructor
@Table(name = "secciones")
@Data   

public class Seccion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "codigo_seccion", unique = true, nullable = false)
    private String codigoSeccion;
    
    @Column(name = "capacidad")
    private int capacidad;
    
    @Column(name = "aula")
    private String aula;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curso_id")
    private Curso curso;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "docente_id")
    private Docente docente;
  
}

