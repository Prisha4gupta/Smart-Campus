package com.sca.smartcampusbackend.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Configuration
 * Configures API documentation for all endpoints
 * 
 * @since 1.0.0
 */
@Configuration
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "JWT Authentication - Enter your token without 'Bearer ' prefix")
public class OpenApiConfig {

    @Bean
    public OpenAPI smartCampusOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Campus Assistant API")
                        .description(
                                "RESTful API for Smart Campus Assistant - A comprehensive campus management system. " +
                                        "This API provides endpoints for managing courses, enrollments, timetables, events, "
                                        +
                                        "notifications, quizzes, and user authentication.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Smart Campus")
                                .email("support@smartcampus.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
