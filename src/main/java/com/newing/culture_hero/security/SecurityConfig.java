package com.newing.culture_hero.security;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth// Temporary for testing
                .requestMatchers("/api/v1/users").permitAll()           // User registration
                .requestMatchers("/api/v1/auth/**").permitAll()         // Authentication endpoints
                .requestMatchers("/actuator/**").permitAll()            // Actuator endpoints
                .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/api/v1/companies/**").permitAll()
                    .requestMatchers("/api/v1/tasks/**").permitAll()
                    .requestMatchers("/api/v1/progress/**").permitAll()
                    .requestMatchers("/api/v1/certificates/**").permitAll()
                    .requestMatchers("/api/v1/leaderboard/**").permitAll()
                    .requestMatchers("/api/v1/reports/**").permitAll()
                    .requestMatchers("/api/v1/streaks/**").permitAll()
                    .requestMatchers("/api/v1/streaks/**/increment").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic.realmName("CultureHero"))
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public Hibernate6Module hibernateModule() {
        Hibernate6Module module = new Hibernate6Module();
        module.disable(Hibernate6Module.Feature.USE_TRANSIENT_ANNOTATION);
        return module;
    }
}
