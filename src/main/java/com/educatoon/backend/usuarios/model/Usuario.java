
package com.educatoon.backend.usuarios.model;

import com.educatoon.backend.utils.AuditBase;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Diego
 */

@Entity
@NoArgsConstructor
@Table(name = "usuarios")
@Data
public class Usuario extends AuditBase{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;        
    
    @Column (name = "email", unique = true, nullable = false)
    private String email;
    
    @Column (name = "password", nullable = false)
    private String password;
    
    @Column (name = "estado")
    private boolean enabled;        
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Rol rol;
    
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Perfil perfil;
}
