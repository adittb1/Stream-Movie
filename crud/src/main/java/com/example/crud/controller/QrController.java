package com.example.crud.controller;

import com.example.crud.Model.User;
import com.example.crud.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class QrController {

    private final UserRepository userRepository;

    // Konstruktor untuk dependency injection UserRepository
    public QrController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/qr-payment")
    public String showQrPage(HttpSession session, Model model) {
        String pendingUsername = (String) session.getAttribute("pendingUser");
        if (pendingUsername == null) {
            return "redirect:/register";
        }
        model.addAttribute("pendingUser", pendingUsername);
        return "qr-payment";
    }

    @GetMapping("/confirm")
    public String confirmRegistration(@RequestParam String accept, HttpSession session, Model model) {
        String pendingUsername = (String) session.getAttribute("pendingUser");

        if (pendingUsername == null) {
            return "redirect:/register";
        }

        User user = userRepository.findByUsername(pendingUsername).orElse(null);

        if (user != null) {
            if ("iya".equalsIgnoreCase(accept)) {
                user.setStatus("PENDING"); // ubah ke PENDING
                model.addAttribute("message", "Pendaftaran Anda sedang diproses. Silakan tunggu konfirmasi admin.");
            } else {
                user.setStatus("INACTIVE");
                model.addAttribute("message", "Pendaftaran dibatalkan.");
            }
            userRepository.save(user);
        }

        session.removeAttribute("pendingUser");
        return "confirm";
    }
}
