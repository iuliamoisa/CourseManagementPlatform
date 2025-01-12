package com.ing.hubs.jpa.security;

import com.ing.hubs.jpa.service.SecurityUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor

public class SecurityConfig {
    private SecurityUserDetailsService userDetailsService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/user/create").hasRole("ADMIN");
                    registry.requestMatchers("/user/all/**", "/user/id/*", "/user/delete/*").hasAnyRole("PROFESSOR", "ADMIN");
                    registry.requestMatchers("/user/**", "/authenticate").permitAll();
                    registry.requestMatchers("/courses/all").permitAll();
                    registry.requestMatchers("/courses/**").hasAnyRole("PROFESSOR", "ADMIN");
                    registry.requestMatchers("/enrollments/update/{id}", "/enrollments/prof/**").hasAnyRole("PROFESSOR", "ADMIN");
                    registry.requestMatchers("/enrollments/create", "/enrollments/current").hasAnyRole("STUDENT", "ADMIN");
                    registry.requestMatchers("/class-activity", "/class-activity/id").hasAnyRole("PROFESSOR", "ADMIN");
                    registry.requestMatchers("/schedule", "/schedule/delete/id", "/schedule/update/id").hasAnyRole("PROFESSOR", "ADMIN");
                    registry.anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public UserDetailsService userDetailsService(){
        return userDetailsService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
