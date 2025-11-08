
package com.educatoon.backend.security.dto;

import java.util.Collection;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author Diego
 */

@Data
public class JwtResponse {
    private String token;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    
    public JwtResponse(String token, String email, Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.email = email;
        this.authorities = authorities;
    }
}
