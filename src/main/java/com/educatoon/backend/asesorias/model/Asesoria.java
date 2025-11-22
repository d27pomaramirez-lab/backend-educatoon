
package com.educatoon.backend.asesorias.model;

import com.educatoon.backend.usuarios.model.Docente;
import com.educatoon.backend.usuarios.model.Estudiante;
import com.educatoon.backend.utils.AuditBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Diego
 */

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "asesorias")
public class Asesoria extends AuditBase{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;
    
    @ManyToOne
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;
    
    //@ManyToOne
    //@JoinColumn(name = "curso_id", nullable = false)
    @Column(name = "curso_id", nullable = false)
    private UUID cursoId;
    
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    
    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;
    
    @Column(nullable = false)
    private String modalidad;

    @Column(name = "enlace_reunion")
    private String enlaceReunion;

    @Column(name = "lugar_presencial")
    private String lugarPresencial;
    
    @Column(nullable = false)
    private String tema;
    
    @Column(name = "areas_refuerzo", columnDefinition = "TEXT")
    private String areasRefuerzo;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones; 

    @Column(name = "asistencia")
    private Boolean asistencia; 

    @Column(nullable = false)
    private String estado;

    
}
