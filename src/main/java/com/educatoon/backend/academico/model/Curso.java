
package com.educatoon.backend.academico.model;

import com.educatoon.backend.utils.AuditBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author hecto
 */

@Entity
@NoArgsConstructor
@Table(name = "cursos")
@Data 
public class Curso extends AuditBase{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "ciclo", nullable = false)
    private String ciclo;
    
    @Column(name = "estado")
    private boolean estado;
    
}
