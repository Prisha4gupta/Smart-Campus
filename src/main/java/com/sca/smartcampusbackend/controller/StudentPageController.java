package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.entity.StudentEnrollment;
import com.sca.smartcampusbackend.entity.User;
import com.sca.smartcampusbackend.repository.StudentEnrollmentRepository;
import com.sca.smartcampusbackend.repository.UserRepository;
import com.sca.smartcampusbackend.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentPageController {

    private final UserRepository userRepository;
    private final StudentEnrollmentRepository enrollmentRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        List<StudentEnrollment> enrollments = enrollmentRepository.findByUser_Id(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("currentPage", "student/profile");
        return "pages/student/profile";
    }

    @GetMapping("/settings")
    public String settings(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("currentPage", "student/settings");
        return "pages/student/settings";
    }

    @GetMapping("/settings/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("currentPage", "student/settings");
        return "pages/student/change-password";
    }

    @PostMapping("/settings/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmNewPassword,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("error", "New passwords do not match");
            return "pages/student/change-password";
        }

        try {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            customUserDetailsService.changePassword(username, currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/login?passwordChanged";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/student/change-password";
        }
    }

    private User getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
