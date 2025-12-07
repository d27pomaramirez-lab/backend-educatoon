package com.educatoon.backend.security.service;

import com.educatoon.backend.usuarios.model.Usuario;
import com.educatoon.backend.usuarios.repository.UsuarioRepository;
import com.educatoon.backend.utils.CustomUserDetails;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Diego
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    //Para iniciar sesión
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(usuario.getRol().getNombre()));

        return new CustomUserDetails(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getPassword(),
            usuario.isEnabled(), 
            authorities
        );
    }
}
