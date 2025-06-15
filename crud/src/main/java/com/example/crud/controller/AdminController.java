package com.example.crud.controller;

import com.example.crud.Model.User;
import com.example.crud.Model.Cinema;
import com.example.crud.Model.Paket;
import com.example.crud.service.UserService;
import com.example.crud.service.CinemaService;
import com.example.crud.service.PaketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final CinemaService cinemaService;
    private final PaketService paketService;

    @Autowired
    // Konstruktor dengan @Autowired untuk menyuntikkan service-service tersebut
    public AdminController(UserService userService, CinemaService cinemaService, PaketService paketService) {
        this.userService = userService;
        this.cinemaService = cinemaService;
        this.paketService = paketService;
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    // Halaman cinema
    @GetMapping("/cinema")
    public String adminCinema(Model model) {
        List<Cinema> films = cinemaService.getAllCinemas();
        model.addAttribute("films", films);
        return "admin/cinema";
    }

    // USER SECTION

    @GetMapping("/user")
    public String listUser(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/user";
    }

    @GetMapping("/user/create")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pakets", paketService.getAllPakets());
        return "admin/user-form";
    }

    // Perbaikan di bagian ini: menggunakan userService, bukan userRepository
    @PostMapping("/user/save")
    public String saveUser(@ModelAttribute("user") User user) {
        if (user.getId() != null) {
            // Mode edit: ambil user lama dari DB untuk mempertahankan tanggalDaftar jika form kosong
            User existingUser = userService.getUserById(user.getId());
            if (user.getTanggalDaftar() == null && existingUser != null) {
                user.setTanggalDaftar(existingUser.getTanggalDaftar());
            }
        } else {
            // Mode create: jika belum ada tanggal, isi otomatis
            if (user.getTanggalDaftar() == null) {
                user.setTanggalDaftar(LocalDate.now());
            }
        }

        userService.saveUser(user);
        return "redirect:/admin/user";
    }

    @GetMapping("/user/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("pakets", paketService.getAllPakets());
        model.addAttribute("user", user);
        return "admin/user-form";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/user";
    }

    // CINEMA SECTION

    @GetMapping("/cinema/list")
    public String listCinema(Model model) {
        List<Cinema> cinemas = cinemaService.getAllCinemas();
        model.addAttribute("cinemas", cinemas);
        return "admin/cinema-list";
    }

    @GetMapping("/cinema/create")
    public String showCinemaAddForm(Model model) {
        model.addAttribute("cinema", new Cinema());
        return "admin/cinema-form";
    }

    @PostMapping("/cinema/save")
    public String saveCinema(@ModelAttribute("cinema") Cinema cinema) {
        cinemaService.saveCinema(cinema);
        return "redirect:/admin/cinema";
    }

    @GetMapping("/cinema/edit/{id}")
    public String showCinemaEditForm(@PathVariable("id") Long id, Model model) {
        Cinema cinema = cinemaService.getCinemaById(id);
        model.addAttribute("cinema", cinema);
        return "admin/cinema-form";
    }

    @PostMapping("/cinema/delete/{id}")
    public String deleteCinema(@PathVariable("id") Long id) {
        cinemaService.deleteCinema(id);
        return "redirect:/admin/cinema";
    }

    // PAKET SECTION

    @GetMapping("/paket")
    public String listPaket(Model model) {
        List<Paket> pakets = paketService.getAllPakets();
        model.addAttribute("pakets", pakets);
        return "admin/paket";
    }

    @GetMapping("/paket/create")
    public String showPaketAddForm(Model model) {
        model.addAttribute("paket", new Paket());
        return "admin/paket-form";
    }

    @PostMapping("/paket/save")
    public String savePaket(@ModelAttribute("paket") Paket paket) {
        paketService.savePaket(paket);
        return "redirect:/admin/paket";
    }

    @GetMapping("/paket/edit/{id}")
    public String showPaketEditForm(@PathVariable("id") Long id, Model model) {
        Paket paket = paketService.getPaketById(id);
        model.addAttribute("paket", paket);
        return "admin/paket-form";
    }

    @PostMapping("/paket/delete/{id}")
    public String deletePaket(@PathVariable("id") Long id) {
        paketService.deletePaket(id);
        return "redirect:/admin/paket";
    }
}
