
package com.educatoon.backend.usuarios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

/**
 *
 * @author Diego
 */

@Entity
@Table(name = "docentes")
@Data
public class Docente {
    @Id
    @Column(name = "usuario_id")
    private UUID id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @Column(name = "especialidad")
    private String especialidad;
}
