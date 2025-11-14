
package com.educatoon.backend.usuarios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Diego
 */

@Entity
@NoArgsConstructor
@Table(name = "estudiantes")
@Data
public class Estudiante {
    @Id
    @Column(name = "usuario_id")
    private UUID id;
   
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId //toma el id que se crea en el usuario
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "codigo_estudiante")
    private String codigoEstudiante;
    
    @Column(name = "fecha_matricula")
    @Temporal(TemporalType.DATE)
    private Date fechaMatricula;
    
    @Column(name = "documentos_validados")
    private boolean documentosValidados;
}
