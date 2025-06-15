package com.example.crud.controller;

import com.example.crud.Model.Cinema;
import com.example.crud.repository.CinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private CinemaRepository cinemaRepository;

    // Existing method yang sudah kamu punya, jangan diubah:
    @GetMapping("/")
    public String homeRedirect(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Ambil semua cinema dari database dan kirim ke view
        List<Cinema> cinemas = cinemaRepository.findAll();
        model.addAttribute("cinemas", cinemas);

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                return "redirect:/admin";
            } else {
                return "redirect:/home";
            }
        }

        return "index"; // Halaman publik jika belum login, dengan cinemas disiapkan
    }

    // Halaman login
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }

    // Halaman dashboard user, bisa diakses oleh USER dan ADMIN
    @GetMapping({"/home"})
    public String userDashboard(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Ambil semua cinema dari database dan kirim ke view
        List<Cinema> cinemas = cinemaRepository.findAll();
        model.addAttribute("cinemas", cinemas);

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            model.addAttribute("username", auth.getName());

        } else {
            model.addAttribute("username", "Guest");
        }


        return "user/dashboard"; // templates/user/dashboard.html
    }

    // Halaman dashboard admin khusus ADMIN
    @GetMapping("/admin")
    public String indexAdmin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            model.addAttribute("username", auth.getName());
        } else {
            model.addAttribute("username", "Admin");
        }
        return "admin/dashboard"; // templates/admin/dashboard.html
    }

}
