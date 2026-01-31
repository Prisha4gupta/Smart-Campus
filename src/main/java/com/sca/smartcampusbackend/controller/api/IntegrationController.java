package com.sca.smartcampusbackend.controller.api;

import com.sca.smartcampusbackend.service.EmailService;
import com.sca.smartcampusbackend.service.external.WeatherService;
import com.sca.smartcampusbackend.service.external.WeatherService.WeatherResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for External Integrations
 * Provides endpoints for Weather API and Email notifications
 * 
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/integrations")
@RequiredArgsConstructor
@Tag(name = "External Integrations", description = "Weather API and Email notification endpoints")
public class IntegrationController {

    private final WeatherService weatherService;
    private final EmailService emailService;

    // ==================== Weather API ====================

    /**
     * Get weather for campus location
     * GET /api/integrations/weather
     */
    @GetMapping("/weather")
    @Operation(summary = "Get campus weather", description = "Get current weather for campus location")
    public ResponseEntity<WeatherResponse> getCampusWeather() {
        WeatherResponse weather = weatherService.getCampusWeather();
        return ResponseEntity.ok(weather);
    }

    /**
     * Get weather for a specific city
     * GET /api/integrations/weather/{city}
     */
    @GetMapping("/weather/{city}")
    @Operation(summary = "Get weather by city", description = "Get current weather for a specific city")
    public ResponseEntity<WeatherResponse> getWeatherByCity(@PathVariable String city) {
        WeatherResponse weather = weatherService.getWeather(city);
        return ResponseEntity.ok(weather);
    }

    // ==================== Email API ====================

    /**
     * Send a test email (Admin only)
     * POST /api/integrations/email/test
     */
    @PostMapping("/email/test")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send test email", description = "Send a test email to verify SMTP configuration (Admin only)")
    public ResponseEntity<Map<String, String>> sendTestEmail(@Valid @RequestBody EmailRequest request) {
        emailService.sendSimpleEmail(
                request.getTo(),
                "Smart Campus - Test Email",
                "This is a test email from Smart Campus Assistant.\n\nIf you received this, email configuration is working correctly!");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Test email sent successfully");
        response.put("to", request.getTo());
        return ResponseEntity.ok(response);
    }

    /**
     * Send notification email
     * POST /api/integrations/email/notify
     */
    @PostMapping("/email/notify")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Send notification email", description = "Send a notification email to a user")
    public ResponseEntity<Map<String, String>> sendNotificationEmail(
            @Valid @RequestBody NotificationEmailRequest request) {
        emailService.sendSimpleEmail(
                request.getTo(),
                request.getSubject(),
                request.getMessage());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification email sent successfully");
        response.put("to", request.getTo());
        return ResponseEntity.ok(response);
    }

    /**
     * Send event reminder emails
     * POST /api/integrations/email/event-reminder
     */
    @PostMapping("/email/event-reminder")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send event reminder", description = "Send event reminder email (Admin only)")
    public ResponseEntity<Map<String, String>> sendEventReminder(@Valid @RequestBody EventReminderRequest request) {
        emailService.sendEventNotification(
                request.getTo(),
                request.getEventName(),
                request.getEventDate());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Event reminder sent successfully");
        response.put("to", request.getTo());
        response.put("event", request.getEventName());
        return ResponseEntity.ok(response);
    }

    // ==================== Request DTOs ====================

    @Data
    public static class EmailRequest {
        @NotBlank(message = "Email address is required")
        @Email(message = "Invalid email address")
        private String to;
    }

    @Data
    public static class NotificationEmailRequest {
        @NotBlank(message = "Email address is required")
        @Email(message = "Invalid email address")
        private String to;

        @NotBlank(message = "Subject is required")
        private String subject;

        @NotBlank(message = "Message is required")
        private String message;
    }

    @Data
    public static class EventReminderRequest {
        @NotBlank(message = "Email address is required")
        @Email(message = "Invalid email address")
        private String to;

        @NotBlank(message = "Event name is required")
        private String eventName;

        @NotBlank(message = "Event date is required")
        private String eventDate;
    }
}
