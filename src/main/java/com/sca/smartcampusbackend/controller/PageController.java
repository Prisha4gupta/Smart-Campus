package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.DashboardResponse;
import com.sca.smartcampusbackend.dto.EventDTO;
import com.sca.smartcampusbackend.dto.SystemStatsDTO;
import com.sca.smartcampusbackend.entity.User;
import com.sca.smartcampusbackend.repository.UserRepository;
import com.sca.smartcampusbackend.service.CustomUserDetailsService;
import com.sca.smartcampusbackend.service.DashboardService;
import com.sca.smartcampusbackend.service.CourseOfferingService;
import com.sca.smartcampusbackend.service.EventService;
import com.sca.smartcampusbackend.service.NotificationService;
import com.sca.smartcampusbackend.repository.StudentEnrollmentRepository;
import com.sca.smartcampusbackend.repository.NotificationRepository;
import com.sca.smartcampusbackend.entity.StudentEnrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for rendering Thymeleaf templates
 * Maps URLs to template pages (server-side rendering)
 * 
 * @version 1.0.0
 */
@Controller
public class PageController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired(required = false)
    private CourseOfferingService offeringService;

    @Autowired(required = false)
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired(required = false)
    private NotificationService notificationService;

    @Autowired
    private StudentEnrollmentRepository enrollmentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Dashboard page - personalized homepage for students and admins
     * Fetches dashboard data from backend API (student-specific for students only)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        
        // Check if user has ADMIN role
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        // Only fetch student-specific dashboard data for non-admin users
        if (!isAdmin) {
            try {
                DashboardResponse dashboardData = dashboardService.getDashboardForStudent(userId);
                model.addAttribute("dashboard", dashboardData);
            } catch (Exception e) {
                // If dashboard service fails, provide empty data
                model.addAttribute("dashboard", new DashboardResponse(null, null, null, null));
            }
        } else {
            // For admin users, fetch events and system statistics
            try {
                List<EventDTO> events = dashboardService.getUpcomingEvents();
                model.addAttribute("dashboard", new DashboardResponse(null, null, events, null));
                
                // Fetch system statistics for admin dashboard
                SystemStatsDTO systemStats = dashboardService.getSystemStats();
                model.addAttribute("systemStats", systemStats);
            } catch (Exception e) {
                model.addAttribute("dashboard", new DashboardResponse(null, null, null, null));
                model.addAttribute("systemStats", new SystemStatsDTO(0L, 0L, 0L, 0L));
            }
        }

        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentPage", "dashboard");
        return "pages/dashboard";
    }

    /**
     * Timetable page - weekly class schedule
     */
    @GetMapping("/timetable")
    public String timetable(Model model, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentPage", "timetable");
        return "pages/timetable";
    }

    /**
     * Events page - campus announcements and events
     */
    @GetMapping("/events")
    public String events(Model model) {
        if (eventService != null) {
            try {
                model.addAttribute("events", eventService.getUpcomingEvents());
            } catch (Exception e) {
                model.addAttribute("events", java.util.Collections.emptyList());
            }
        }
        model.addAttribute("currentPage", "events");
        return "pages/events";
    }

    /**
     * Enrollment page - browse and enroll in course offerings
     */
    @GetMapping("/enroll")
    public String enroll(Model model, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);

        if (offeringService != null) {
            try {
                model.addAttribute("offerings", offeringService.getAllOfferings());
            } catch (Exception e) {
                model.addAttribute("offerings", java.util.Collections.emptyList());
            }
        }

        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentPage", "enroll");
        return "pages/enroll";
    }

    /**
     * Notifications page - view all notifications
     */
    @GetMapping("/notifications")
    public String notifications(Model model, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentPage", "notifications");
        return "pages/notifications";
    }

    /**
     * Profile page - user profile and settings
     */
    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);

        // Count active enrollments
        long enrolledCount = enrollmentRepository.findByUser_IdAndStatus(
                userId, StudentEnrollment.EnrollmentStatus.ENROLLED).size();

        // Count events attended (event notifications for this user)
        long eventsCount = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .filter(n -> n.getEvent() != null)
                .map(n -> n.getEvent().getId())
                .distinct()
                .count();

        // Count unread notifications
        long unreadNotifications = notificationRepository.countUnreadByUserId(userId);

        model.addAttribute("enrolledCount", enrolledCount);
        model.addAttribute("eventsCount", eventsCount);
        model.addAttribute("notificationCount", unreadNotifications);
        model.addAttribute("currentPage", "profile");
        return "pages/profile";
    }

    /**
     * Settings page - application preferences
     */
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("currentPage", "settings");
        return "pages/settings";
    }

    /**
     * Change password page
     */
    @GetMapping("/settings/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("currentPage", "settings");
        return "pages/change-password";
    }

    /**
     * Handle password change request
     */
    @PostMapping("/settings/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmNewPassword,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validate password confirmation
        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("error", "New passwords do not match");
            model.addAttribute("currentPage", "settings");
            return "pages/change-password";
        }

        try {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            customUserDetailsService.changePassword(username, currentPassword, newPassword);

            // Get user ID for notification
            Long userId = getUserIdFromAuthentication(authentication);

            // Create security notification
            if (notificationService != null) {
                try {
                    notificationService.createNotification(
                            userId,
                            null,
                            "Password Changed",
                            "Your password was changed successfully. If this wasn't you, contact support immediately.");
                } catch (Exception e) {
                    // Silently fail notification creation - password change succeeded
                }
            }

            // Invalidate session to force re-authentication with new password
            authentication.setAuthenticated(false);

            redirectAttributes.addFlashAttribute("success",
                    "Password changed successfully! Please log in with your new password.");
            return "redirect:/login?passwordChanged";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("currentPage", "settings");
            return "pages/change-password";
        }
    }

    // ===== Admin Pages =====

    /**
     * Admin dashboard - redirects to offerings management
     */
    @GetMapping("/admin")
    public String adminDashboard() {
        return "redirect:/admin/offerings";
    }

    /**
     * Admin offerings list page
     */
    @GetMapping("/admin/offerings")
    public String adminOfferings(Model model) {
        if (offeringService != null) {
            try {
                model.addAttribute("offerings", offeringService.getAllOfferings());
            } catch (Exception e) {
                model.addAttribute("offerings", java.util.Collections.emptyList());
            }
        }
        model.addAttribute("currentPage", "admin/offerings");
        return "admin/offerings-list";
    }

    /**
     * Admin offering create page
     */
    @GetMapping("/admin/offerings/create")
    public String adminOfferingCreate(Model model) {
        model.addAttribute("currentPage", "admin/offerings");
        return "admin/offering-create";
    }

    /**
     * Admin offering edit page
     */
    @GetMapping("/admin/offerings/{id}/edit")
    public String adminOfferingEdit(@PathVariable Long id, Model model) {
        if (offeringService != null) {
            try {
                model.addAttribute("offering", offeringService.getOfferingById(id));
            } catch (Exception e) {
                return "redirect:/admin/offerings";
            }
        }
        model.addAttribute("currentPage", "admin/offerings");
        return "admin/offering-create";
    }

    /**
     * Admin events list page
     */
    @GetMapping("/admin/events")
    public String adminEvents(Model model) {
        if (eventService != null) {
            try {
                model.addAttribute("events", eventService.getAllEvents());
            } catch (Exception e) {
                model.addAttribute("events", java.util.Collections.emptyList());
            }
        }
        model.addAttribute("currentPage", "admin/events");
        return "admin/events-list";
    }

    /**
     * Admin event create page
     */
    @GetMapping("/admin/events/create")
    public String adminEventCreate(Model model, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentPage", "admin/events");
        return "admin/event-create";
    }

    /**
     * Admin courses list page
     */
    @GetMapping("/admin/courses")
    public String adminCourses(Model model) {
        model.addAttribute("currentPage", "admin/courses");
        return "admin/courses-list";
    }

    /**
     * Admin course create page
     */
    @GetMapping("/admin/courses/create")
    public String adminCourseCreate(Model model) {
        model.addAttribute("currentPage", "admin/courses");
        return "admin/course-create";
    }

    /**
     * Admin course edit page
     */
    @GetMapping("/admin/courses/{id}/edit")
    public String adminCourseEdit(@PathVariable Long id, Model model) {
        model.addAttribute("currentPage", "admin/courses");
        model.addAttribute("courseId", id);
        return "admin/course-create";
    }

    /**
     * Admin faculty list page
     */
    @GetMapping("/admin/faculty")
    public String adminFaculty(Model model) {
        model.addAttribute("currentPage", "admin/faculty");
        return "admin/faculty-list";
    }

    /**
     * Admin faculty create page
     */
    @GetMapping("/admin/faculty/create")
    public String adminFacultyCreate(Model model) {
        model.addAttribute("currentPage", "admin/faculty");
        return "admin/faculty-create";
    }

    /**
     * Admin faculty edit page
     */
    @GetMapping("/admin/faculty/{id}/edit")
    public String adminFacultyEdit(@PathVariable Long id, Model model) {
        model.addAttribute("currentPage", "admin/faculty");
        model.addAttribute("facultyId", id);
        return "admin/faculty-create";
    }

    /**
     * Admin timetable list page
     */
    @GetMapping("/admin/timetable")
    public String adminTimetable(Model model) {
        model.addAttribute("currentPage", "admin/timetable");
        return "admin/timetable-list";
    }

    /**
     * Admin timetable create page
     */
    @GetMapping("/admin/timetable/create")
    public String adminTimetableCreate(Model model) {
        model.addAttribute("currentPage", "admin/timetable");
        return "admin/timetable-create";
    }

    /**
     * Admin timetable edit page
     */
    @GetMapping("/admin/timetable/{id}/edit")
    public String adminTimetableEdit(@PathVariable Long id, Model model) {
        model.addAttribute("currentPage", "admin/timetable");
        model.addAttribute("entryId", id);
        return "admin/timetable-create";
    }

    /**
     * Helper method to extract user ID from Spring Security Authentication
     * 
     * @param authentication Spring Security authentication object
     * @return User ID (defaults to 1 if not found)
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // Look up user by username to get ID
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
            return user.getId();
        }

        // Fallback: return 1 (first user) for testing
        // In production, this should redirect to login or throw an exception
        return 1L;
    }
}
