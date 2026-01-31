package com.sca.smartcampusbackend.config;

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
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                // Public resources
                                                .requestMatchers("/login", "/signup", "/register", "/error").permitAll()
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**",
                                                                "/webjars/**")
                                                .permitAll()
                                                
                                // Admin-only pages and APIs
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                
                                // Student-only pages and APIs
                                .requestMatchers("/enroll", "/timetable", "/notifications", 
                                                "/student/**", "/api/enrollments/**").hasRole("STUDENT")
                                
                                // Timetable API - accessible by both ADMIN and STUDENT
                                .requestMatchers("/api/timetable-entries/**").hasAnyRole("ADMIN", "STUDENT")
                                
                                // Shared pages (both roles can access)
                                .requestMatchers("/dashboard", "/profile", "/settings/**", 
                                                                "/events", "/api/events/**", "/api/dashboard/**",
                                                                "/api/notifications/**").authenticated()
                                                
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .usernameParameter("username")
                                                .passwordParameter("password")
                                                .defaultSuccessUrl("/dashboard", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll());

                return http.build();
        }
}
