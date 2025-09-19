package com.mohamed.backend.utils.security;

import com.mohamed.backend.utils.filter.CorrelationIdFilter;
import com.mohamed.backend.utils.filter.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private CorrelationIdFilter correlationIdFilter;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(correlationIdFilter, org.springframework.security.web.access.ExceptionTranslationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/supervisor/**").hasAnyRole("ADMIN","SUPERVISOR")
                        .requestMatchers("/instructor/**").hasAnyRole("ADMIN","INSTRUCTOR","SUPERVISOR")
                        .requestMatchers("/all/**").hasAnyRole("ADMIN","INSTRUCTOR","SUPERVISOR")
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }


}
