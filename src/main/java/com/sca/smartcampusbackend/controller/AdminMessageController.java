package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.message.ConversationPreviewDTO;
import com.sca.smartcampusbackend.dto.message.MessageDTO;
import com.sca.smartcampusbackend.entity.User;
import com.sca.smartcampusbackend.repository.UserRepository;
import com.sca.smartcampusbackend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/messages")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    @GetMapping
    public String inbox(Model model, Authentication authentication) {
        Long userId = getUserId(authentication);
        List<ConversationPreviewDTO> conversations = messageService.getInbox(userId);
        List<MessageDTO> broadcasts = messageService.getBroadcasts();

        model.addAttribute("conversations", conversations);
        model.addAttribute("broadcasts", broadcasts);
        model.addAttribute("currentPage", "admin/messages");
        return "admin/messages/inbox";
    }

    @GetMapping("/broadcast")
    public String broadcastForm(Model model) {
        model.addAttribute("currentPage", "admin/messages");
        return "admin/messages/broadcast";
    }

    @PostMapping("/broadcast")
    public String sendBroadcast(@RequestParam String content,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Long senderId = getUserId(authentication);
            messageService.sendBroadcast(senderId, content);
            redirectAttributes.addFlashAttribute("success", "Broadcast sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send broadcast: " + e.getMessage());
        }
        return "redirect:/admin/messages";
    }

    @GetMapping("/chat/{studentId}")
    public String chat(@PathVariable Long studentId, Model model, Authentication authentication) {
        Long adminId = getUserId(authentication);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        List<MessageDTO> messages = messageService.getConversation(adminId, studentId);
        messageService.markConversationAsRead(adminId, studentId);

        model.addAttribute("messages", messages);
        model.addAttribute("otherUser", student);
        model.addAttribute("currentUserId", adminId);
        model.addAttribute("currentPage", "admin/messages");
        return "admin/messages/chat";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam Long receiverId,
            @RequestParam String content,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Long senderId = getUserId(authentication);
            messageService.sendToUser(senderId, receiverId, content);
            redirectAttributes.addFlashAttribute("success", "Message sent!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send message: " + e.getMessage());
        }
        return "redirect:/admin/messages/chat/" + receiverId;
    }

    @GetMapping("/compose")
    public String composeForm(Model model) {
        List<User> students = userRepository.findAll().stream()
                .filter(u -> "STUDENT".equals(u.getRole()))
                .toList();
        model.addAttribute("students", students);
        model.addAttribute("currentPage", "admin/messages");
        return "admin/messages/compose";
    }

    @PostMapping("/compose")
    public String composeAndSend(@RequestParam Long receiverId,
            @RequestParam String content,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Long senderId = getUserId(authentication);
            messageService.sendToUser(senderId, receiverId, content);
            redirectAttributes.addFlashAttribute("success", "Message sent!");
            return "redirect:/admin/messages/chat/" + receiverId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send message: " + e.getMessage());
            return "redirect:/admin/messages/compose";
        }
    }

    private Long getUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
    }
}
