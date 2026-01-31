package com.sca.smartcampusbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Email Service for sending notifications
 * Supports both simple text emails and HTML emails
 * 
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@smartcampus.com}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    /**
     * Send a simple text email
     * 
     * @param to      Recipient email address
     * @param subject Email subject
     * @param body    Email body text
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String body) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send to: {}, Subject: {}", to, subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    /**
     * Send an HTML email
     * 
     * @param to       Recipient email address
     * @param subject  Email subject
     * @param htmlBody HTML content
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send HTML to: {}, Subject: {}", to, subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
        }
    }

    /**
     * Send enrollment confirmation email
     */
    public void sendEnrollmentConfirmation(String to, String studentName, String courseName) {
        String subject = "Enrollment Confirmation - " + courseName;
        String body = String.format(
                "Dear %s,\n\n" +
                        "You have been successfully enrolled in %s.\n\n" +
                        "Please check your timetable for class schedules.\n\n" +
                        "Best regards,\n" +
                        "Smart Campus Team",
                studentName, courseName);
        sendSimpleEmail(to, subject, body);
    }

    /**
     * Send event notification email
     */
    public void sendEventNotification(String to, String eventName, String eventDate) {
        String subject = "Upcoming Event: " + eventName;
        String body = String.format(
                "Hello,\n\n" +
                        "This is a reminder about the upcoming event:\n\n" +
                        "Event: %s\n" +
                        "Date: %s\n\n" +
                        "Don't miss it!\n\n" +
                        "Best regards,\n" +
                        "Smart Campus Team",
                eventName, eventDate);
        sendSimpleEmail(to, subject, body);
    }

    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(String to, String resetLink) {
        String subject = "Password Reset Request - Smart Campus";
        String body = String.format(
                "Hello,\n\n" +
                        "We received a request to reset your password.\n\n" +
                        "Click the link below to reset your password:\n" +
                        "%s\n\n" +
                        "If you didn't request this, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "Smart Campus Team",
                resetLink);
        sendSimpleEmail(to, subject, body);
    }
}
