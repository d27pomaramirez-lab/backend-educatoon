
package com.educatoon.backend.security.jwt;

import com.educatoon.backend.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 *
 * @author Diego
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Ignoramos si no hay header o no empieza con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el token
        jwt = authHeader.substring(7);
        userEmail = jwtUtil.extractUsername(jwt);

        // Si tenemos email y no hay nadie autenticado en el contexto
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Cargamos el usuario desde la BD
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Si el token es válido
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Autenticamos al usuario para este caso
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Pasamos la petición al siguiente filtro
        filterChain.doFilter(request, response);
    }
}