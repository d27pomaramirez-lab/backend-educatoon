
package com.educatoon.backend.usuarios.model;

import com.educatoon.backend.utils.AuditBase;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "perfiles")
@Data
public class Perfil extends AuditBase{
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @Column (name = "nombres")
    private String nombres;
    
    @Column (name = "apellidos")
    private String apellidos;
    
    @Column(name = "dni")
    private String dni;

    @Column(name = "telefono")
    private String telefono;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @JsonBackReference
    private Usuario usuario;
    
    @Column(name = "sexo")
    private String sexo;

    @Column(name = "estado_civil")
    private String estadoCivil;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;
}
