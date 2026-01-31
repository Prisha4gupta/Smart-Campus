package com.sca.smartcampusbackend.controller.api;

import com.sca.smartcampusbackend.dto.auth.LoginRequest;
import com.sca.smartcampusbackend.dto.auth.LoginResponse;
import com.sca.smartcampusbackend.security.CustomUserDetails;
import com.sca.smartcampusbackend.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for JWT Authentication
 * Provides endpoints for user authentication and token generation
 * 
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "JWT Authentication endpoints")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Authenticate user and return JWT token
     * POST /api/auth/login
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Validates credentials and returns JWT token")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        LoginResponse response = LoginResponse.builder()
                .token(jwt)
                .type("Bearer")
                .username(userDetails.getUsername())
                .role(userDetails.getRole())
                .build();

        return ResponseEntity.ok(response);
    }
}
