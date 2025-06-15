package com.example.crud.controller;

import com.example.crud.Model.Paket;
import com.example.crud.Model.User;
import com.example.crud.repository.PaketRepository;
import com.example.crud.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuthController {

    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaketRepository paketRepository;

    // Konstruktor untuk dependency injection repository dan encoder
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, PaketRepository paketRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.paketRepository = paketRepository;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        // Ambil semua paket dari database
        List<Paket> listPaket = paketRepository.findAll();
        model.addAttribute("pakets", listPaket);  // Sesuai nama variabel di HTML

        return "register"; // nama file thymeleaf register.html
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               @RequestParam("paketId") Long paketId,
                               HttpSession session) {
        // Cek apakah username sudah ada
        if (userRepository.existsByUsername(user.getUsername())) {
            return "redirect:/register?error";
        }

        // Cari paket berdasar ID
        Paket paket = paketRepository.findById(paketId).orElse(null);
        if (paket == null) {
            return "redirect:/register?error";
        }

        // Cek kuota paket
        if (paket.getKuota() <= 0) {
            return "redirect:/register?paketHabis";
        }

        // Enkripsi password dan set role
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setPaket(paket);

        // Simpan user ke database
        userRepository.save(user);

        // Kurangi kuota paket dan simpan update paket
        paket.setKuota(paket.getKuota() - 1);
        paketRepository.save(paket);

        // Simpan username ke session (opsional)
        session.setAttribute("pendingUser", user.getUsername());

        // Redirect ke halaman qr-payment (sesuaikan dengan kebutuhan)
        return "redirect:/qr-payment";
    }
}
