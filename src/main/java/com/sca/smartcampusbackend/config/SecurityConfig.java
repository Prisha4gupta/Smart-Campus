package com.sca.smartcampusbackend.config;

import com.sca.smartcampusbackend.security.AuthTokenFilter;
import com.sca.smartcampusbackend.security.JwtAuthenticationEntryPoint;
import com.sca.smartcampusbackend.security.RateLimitingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration for JWT-based stateless authentication
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final AuthTokenFilter authTokenFilter;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
        private final RateLimitingFilter rateLimitingFilter;

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
                                // Disable CSRF for stateless JWT authentication
                                .csrf(csrf -> csrf.disable())

                                // Configure exception handling
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))

                                // Set session management to stateless
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth
                                                // Public resources
                                                .requestMatchers("/login", "/signup", "/register", "/error").permitAll()
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**",
                                                                "/webjars/**")
                                                .permitAll()

                                                // Swagger/OpenAPI endpoints
                                                .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                                                                "/v3/api-docs/**", "/swagger-resources/**")
                                                .permitAll()

                                                // Public auth API endpoint
                                                .requestMatchers("/api/auth/**").permitAll()

                                                // Weather API - public for demo
                                                .requestMatchers("/api/integrations/weather",
                                                                "/api/integrations/weather/**")
                                                .permitAll()

                                                // File download - public (upload requires auth)
                                                .requestMatchers("/api/files/download/**").permitAll()

                                                // Admin-only pages and APIs
                                                .requestMatchers("/admin/**").hasRole("ADMIN")

                                                // Student-only pages and APIs
                                                .requestMatchers("/enroll", "/timetable", "/notifications",
                                                                "/student/**", "/api/enrollments/**")
                                                .hasRole("STUDENT")

                                                // Timetable API - accessible by both ADMIN and STUDENT
                                                .requestMatchers("/api/timetable-entries/**")
                                                .hasAnyRole("ADMIN", "STUDENT")

                                                // Shared pages (both roles can access)
                                                .requestMatchers("/dashboard", "/profile", "/settings/**",
                                                                "/events", "/api/events/**", "/api/dashboard/**",
                                                                "/api/notifications/**")
                                                .authenticated()

                                                .anyRequest().authenticated())

                                // Add rate limiting filter first
                                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)

                                // Add JWT filter before UsernamePasswordAuthenticationFilter
                                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
