package com.staymanager.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/bookings").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/accommodations/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/accommodations/**").hasRole("HOST")
                .requestMatchers(HttpMethod.PUT, "/api/accommodations/**").hasRole("HOST")
                .requestMatchers(HttpMethod.DELETE, "/api/accommodations/**").hasRole("ADMIN")
                .anyRequest().authenticated());
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());
        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
        );
        return http.build();
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");

            if (role == null || role.isBlank()) {
                return List.of();
            }

            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        });

        return converter;
    }
}
