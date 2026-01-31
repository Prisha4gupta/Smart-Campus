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
@RequestMapping("/student/messages")
@PreAuthorize("hasRole('STUDENT')")
public class StudentMessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    @GetMapping
    public String inbox(Model model, Authentication authentication) {
        Long userId = getUserId(authentication);
        List<ConversationPreviewDTO> conversations = messageService.getInbox(userId);
        List<MessageDTO> broadcasts = messageService.getBroadcasts();
        long unreadCount = messageService.getUnreadCount(userId);

        model.addAttribute("conversations", conversations);
        model.addAttribute("broadcasts", broadcasts);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("currentPage", "student/messages");
        return "pages/student/messages/inbox";
    }

    @GetMapping("/chat/{receiverId}")
    public String chat(@PathVariable Long receiverId, Model model, Authentication authentication) {
        Long userId = getUserId(authentication);
        User otherUser = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<MessageDTO> messages = messageService.getConversation(userId, receiverId);
        messageService.markConversationAsRead(userId, receiverId);

        model.addAttribute("messages", messages);
        model.addAttribute("otherUser", otherUser);
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentPage", "student/messages");
        return "pages/student/messages/chat";
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
        return "redirect:/student/messages/chat/" + receiverId;
    }

    @GetMapping("/compose")
    public String composeForm(Model model) {
        List<User> students = userRepository.findAll().stream()
                .filter(u -> "STUDENT".equals(u.getRole()))
                .toList();
        model.addAttribute("students", students);
        model.addAttribute("currentPage", "student/messages");
        return "pages/student/messages/compose";
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
            return "redirect:/student/messages/chat/" + receiverId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send: " + e.getMessage());
            return "redirect:/student/messages/compose";
        }
    }

    private Long getUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
    }
}
