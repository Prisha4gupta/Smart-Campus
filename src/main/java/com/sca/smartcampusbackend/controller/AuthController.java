package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.entity.User;
import com.sca.smartcampusbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Show Login Page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Points to login.html
    }

    // Show Signup Page
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new User()); // Empty object for the form
        return "signup"; // Points to signup.html
    }

    // Process Signup Form
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        // 1. Encrypt the password before saving!
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        // 2. Default role (if not selected)
        if (user.getRole() == null) {
            user.setRole("STUDENT");
        }

        // 3. Save to Database
        userRepository.save(user);

        return "redirect:/login?success"; // Send them to login after signup
    }
}