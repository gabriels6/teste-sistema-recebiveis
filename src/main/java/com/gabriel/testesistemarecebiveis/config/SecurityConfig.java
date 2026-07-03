package com.gabriel.testesistemarecebiveis.config;

import com.gabriel.testesistemarecebiveis.security.JwtAuthenticationFilter;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
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

/**
 * Configuração de segurança da aplicação, responsável pela cadeia de
 * filtros de segurança, política de sessão e codificação de senhas.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Filtro de autenticação JWT aplicado às requisições. */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Padrões de origem permitidos para requisições cross-origin (CORS).
     * Configurável via {@code app.cors.allowed-origins} (lista separada por
     * vírgula, com suporte a curingas via {@code setAllowedOriginPatterns}).
     * Por padrão libera qualquer porta em {@code localhost} e {@code 127.0.0.1},
     * cobrindo os dev servers de front-end sem depender da porta exata.
     */
    @Value("${app.cors.allowed-origins:"
            + "http://localhost:[*],http://127.0.0.1:[*]}")
    private List<String> allowedOrigins;

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
                .cors(Customizer.withDefaults())
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
     * Impede que o {@link JwtAuthenticationFilter} (anotado com
     * {@code @Component}) seja registrado automaticamente como filtro do
     * container de servlet. Sem isso, ele executaria duas vezes por requisição
     * — uma dentro da cadeia do Spring Security e outra fora dela, antes do
     * {@code CorsFilter} — o que causa autenticação em duplicidade e
     * comportamento inconsistente de CORS. O filtro continua ativo apenas onde
     * é adicionado explicitamente em {@link #securityFilterChain(HttpSecurity)}.
     *
     * @return o registro do filtro com a auto-registração desabilitada
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter>
            jwtAuthenticationFilterRegistration() {
        final FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(jwtAuthenticationFilter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * Define a política de CORS da aplicação, permitindo que o front-end
     * (executado em outra origem) consuma a API. As origens são configuráveis
     * via {@code app.cors.allowed-origins}.
     *
     * @return a fonte de configuração de CORS aplicada a todas as rotas
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        final UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
