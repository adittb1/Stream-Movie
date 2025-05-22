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

    // Arahkan ke halaman mahasiswa saat akses ke root
    @GetMapping("/")
    public String home() {
        return "redirect:/mahasiswa";
    }

    // Menampilkan daftar mahasiswa (khusus user yang sudah login)
    @GetMapping("/mahasiswa")
    @PreAuthorize("isAuthenticated()")
    public String index(Model model) {
        model.addAttribute("daftarMahasiswa", repo.findAll());

        // Tambahkan informasi user yang sedang login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());

        return "index"; // Akan mencari file src/main/resources/templates/index.html
    }

    @GetMapping("/admin_mahasiswa")
    @PreAuthorize("isAuthenticated()")
    public String indexAdmin(Model model) {
        model.addAttribute("daftarMahasiswa", repo.findAll());

        // Tambahkan informasi user yang sedang login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());

        return "admin/dashboard"; // Akan mencari file src/main/resources/templates/index.html
    }

    // Tampilkan form tambah mahasiswa
    @GetMapping("/tambah")
    @PreAuthorize("isAuthenticated()")
    public String showForm(Mahasiswa mahasiswa) {
        return "tambah"; // Akan mencari file src/main/resources/templates/tambah.html
    }

    // Simpan data mahasiswa dari form tambah
    @PostMapping("/tambah")
    @PreAuthorize("isAuthenticated()")
    public String simpanMahasiswa(Mahasiswa mahasiswa) {
        repo.save(mahasiswa);
        return "redirect:/mahasiswa";
    }

    // Tampilkan halaman konfirmasi hapus
    @GetMapping("/hapus/{nrp}")
    @PreAuthorize("isAuthenticated()")
    public String konfirmasiHapus(@PathVariable("nrp") String nrp, Model model) {
        Mahasiswa mahasiswa = repo.findById(nrp).orElse(null);
        if (mahasiswa == null) {
            return "redirect:/mahasiswa";
        }
        model.addAttribute("mahasiswa", mahasiswa);
        return "hapus"; // Akan mencari file src/main/resources/templates/hapus.html
    }

    // Proses hapus mahasiswa
    @PostMapping("/hapus/{nrp}")
    @PreAuthorize("isAuthenticated()")
    public String hapusMahasiswa(@PathVariable("nrp") String nrp) {
        repo.deleteById(nrp);
        return "redirect:/mahasiswa";
    }

    // Tampilkan form edit mahasiswa
    @GetMapping("/edit/{nrp}")
    @PreAuthorize("isAuthenticated()")
    public String showEditForm(@PathVariable("nrp") String nrp, Model model) {
        Mahasiswa mahasiswa = repo.findById(nrp)
                .orElseThrow(() -> new IllegalArgumentException("NRP tidak ditemukan: " + nrp));
        model.addAttribute("mahasiswa", mahasiswa);
        return "edit"; // Akan mencari file src/main/resources/templates/edit.html
    }

    // Simpan perubahan edit mahasiswa
    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String updateMahasiswa(Mahasiswa mahasiswa) {
        repo.save(mahasiswa);
        return "redirect:/mahasiswa";
    }

    // Tampilkan halaman login
   @GetMapping("/login")
public String login() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
    // if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
    //     // Cek apakah user memiliki role ADMIN
    //     boolean isAdmin = auth.getAuthorities().stream()
    //             .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    //     if (isAdmin) {
    //         return "redirect:/admin/dashboard";
    //     } else {
    //         return "redirect:/admin/dashboard";
    //     }
    // }

    return "login"; // Akan merender templates/login.html
}

// @PostMapping("/store_login")
// public String loginPost() {
//     Authentication auth = SecurityContextHolder.getContext().getAuthentication();

//     if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
//         boolean isAdmin = auth.getAuthorities().stream()
//                 .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

//         if (isAdmin) {
//             return "redirect:/admin/dashboard";
//         } else {
//             return "redirect:/mahasiswa/dashboard";
//         }
//     }

//     // Jika belum login atau gagal
//     return "redirect:/login?error";
// }

}
