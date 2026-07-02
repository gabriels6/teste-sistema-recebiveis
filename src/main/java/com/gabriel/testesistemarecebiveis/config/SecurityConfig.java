package com.gabriel.testesistemarecebiveis.config;

import com.gabriel.testesistemarecebiveis.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração de segurança da aplicação, responsável pela cadeia de
 * filtros de segurança, política de sessão e codificação de senhas.
 */
@Configuration
@EnableWebSecurity
public final class SecurityConfig {

    /** Filtro de autenticação JWT aplicado às requisições. */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Cria a configuração de segurança.
     *
     * @param authenticationFilter o filtro de autenticação JWT
     */
    public SecurityConfig(
            final JwtAuthenticationFilter authenticationFilter) {
        this.jwtAuthenticationFilter = authenticationFilter;
    }

    /**
     * Define a cadeia de filtros de segurança da aplicação.
     *
     * @param http o objeto de configuração de segurança HTTP
     * @return a cadeia de filtros de segurança configurada
     * @throws Exception caso ocorra erro ao construir a configuração
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            final HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                HttpMethod.POST, "/api/auth/login")
                        .permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/**/*.css",
                                "/**/*.js")
                        .permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Fornece o codificador de senhas baseado em BCrypt.
     *
     * @return o codificador de senhas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
