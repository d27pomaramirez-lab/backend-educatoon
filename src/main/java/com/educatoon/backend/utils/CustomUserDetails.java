
package com.educatoon.backend.utils;

import java.util.Collection;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 *
 * @author Diego
 */
public class CustomUserDetails extends User {
    
    private final UUID id;

    public CustomUserDetails(
            UUID id,
            String username,
            String password,
            boolean enabled, 
            Collection<? extends GrantedAuthority> authorities) {
        
        super(username, password, enabled, true, true, true, authorities);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
