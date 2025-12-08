package com.educatoon.backend.security.config;

import com.educatoon.backend.security.jwt.JwtAuthenticationFilter;
import com.educatoon.backend.security.service.UserDetailsServiceImpl;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

/**
 *
 * @author Diego
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Bean para encriptar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean del AuthenticationManager para el login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Bean del SecurityFilterChain (las reglas principales)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))            
            .authorizeHttpRequests(authz -> authz
            .requestMatchers("/uploads/**").permitAll()
            .requestMatchers("/api/perfil/foto/**").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/cursos/**").hasAnyAuthority("ROL_COORDINADOR", "ROL_ADMINISTRADOR")
            .requestMatchers("/api/pruebas-entrada/**").permitAll()
            .requestMatchers("/api/matriculas/**").permitAll()
            .requestMatchers("/api/cursos/**").hasAnyAuthority("ROL_COORDINADOR")
            .requestMatchers("/api/coordinador/**").hasAnyAuthority("ROL_COORDINADOR")
            .requestMatchers("/api/docente/**").hasAnyAuthority("ROL_DOCENTE")
            .requestMatchers("/api/estudiante/**").hasAnyAuthority("ROL_ESTUDIANTE")
            .requestMatchers("/api/admin/**").hasAuthority("ROL_ADMINISTRADOR") 
            .anyRequest().authenticated()
        )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 

        return source;
    }
}
