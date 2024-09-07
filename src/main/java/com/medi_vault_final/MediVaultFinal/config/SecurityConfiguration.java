package com.medi_vault_final.MediVaultFinal.config;

import com.medi_vault_final.MediVaultFinal.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers("/api/v1/auth/**", "/api/v1/templates/auth/**", "/css/**", "/js/**")
                        .permitAll()
                        .requestMatchers("/api/v1/prescriptions/any/**").hasAnyRole(Role.ADMIN.name(), Role.DOCTOR.name(), Role.PATIENT.name())
                        .requestMatchers("/api/v1/prescriptions/doctor/**").hasRole(Role.DOCTOR.name())
                        .requestMatchers("/api/v1/prescriptions/admin/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/api/v1/users/any/**").hasAnyRole(Role.DOCTOR.name(), Role.PATIENT.name(), Role.ADMIN.name())
                        .requestMatchers("/api/v1/users/admin/**").hasRole(Role.ADMIN.name())
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement( session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
