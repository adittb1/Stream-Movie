package com.example.crud.controller;

import com.example.crud.Model.Mahasiswa;
import com.example.crud.repository.MahasiswaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MahasiswaController {

    @Autowired
    private MahasiswaRepository repo;

    // Arahkan root ("/") ke halaman index jika belum login, atau redirect jika sudah login
    @GetMapping("/")
    public String homeRedirect(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                return "redirect:/admin_mahasiswa";
            } else {
                return "redirect:/mahasiswa";
            }
        }

        // Belum login, tampilkan halaman publik (index umum)
        return "index";
    }

    // Login page
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }

    // Daftar mahasiswa untuk USER/ADMIN
    @GetMapping("/mahasiswa")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String index(Model model) {
        model.addAttribute("daftarMahasiswa", repo.findAll());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());

        return "user/dashboard"; // templates/mahasiswa/index.html
    }

    // Daftar mahasiswa khusus ADMIN
    @GetMapping("/admin_mahasiswa")
    @PreAuthorize("hasRole('ADMIN')")
    public String indexAdmin(Model model) {
        model.addAttribute("daftarMahasiswa", repo.findAll());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());

        return "admin/dashboard"; // templates/admin/dashboard.html
    }

    @GetMapping("/tambah")
    @PreAuthorize("isAuthenticated()")
    public String showForm(Mahasiswa mahasiswa) {
        return "tambah"; // templates/tambah.html
    }

    @PostMapping("/tambah")
    @PreAuthorize("isAuthenticated()")
    public String simpanMahasiswa(Mahasiswa mahasiswa) {
        repo.save(mahasiswa);
        return "redirect:/mahasiswa";
    }

    @GetMapping("/edit/{nrp}")
    @PreAuthorize("isAuthenticated()")
    public String showEditForm(@PathVariable("nrp") String nrp, Model model) {
        Mahasiswa mahasiswa = repo.findById(nrp)
                .orElseThrow(() -> new IllegalArgumentException("NRP tidak ditemukan: " + nrp));
        model.addAttribute("mahasiswa", mahasiswa);
        return "edit"; // templates/edit.html
    }

    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String updateMahasiswa(Mahasiswa mahasiswa) {
        repo.save(mahasiswa);
        return "redirect:/mahasiswa";
    }

    @GetMapping("/hapus/{nrp}")
    @PreAuthorize("isAuthenticated()")
    public String konfirmasiHapus(@PathVariable("nrp") String nrp, Model model) {
        Mahasiswa mahasiswa = repo.findById(nrp).orElse(null);
        if (mahasiswa == null) {
            return "redirect:/mahasiswa";
        }
        model.addAttribute("mahasiswa", mahasiswa);
        return "hapus"; // templates/hapus.html
    }

    @PostMapping("/hapus/{nrp}")
    @PreAuthorize("isAuthenticated()")
    public String hapusMahasiswa(@PathVariable("nrp") String nrp) {
        repo.deleteById(nrp);
        return "redirect:/mahasiswa";
    }
}
