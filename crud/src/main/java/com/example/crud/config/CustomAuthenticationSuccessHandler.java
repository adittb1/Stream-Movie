package com.example.crud.config;

import com.example.crud.Model.User;
import com.example.crud.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

// Kelas CustomAuthenticationSuccessHandler mengimplementasikan interface AuthenticationSuccessHandler
// ini adalah implementasi inheritance (pewarisan interface)
// Kelas ini juga memiliki atribut UserRepository yang disuntikkan melalui konstruktor,
// ini adalah implementasi agregasi (aggregation)
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    // Agregasi: UserRepository disuntikkan dari luar (dependency injection)
    private final UserRepository userRepository;

    /**
     * Konstruktor menerima UserRepository sebagai dependency injection.
     * Ini contoh agregasi karena UserRepository hidup terpisah,
     * dan kelas ini hanya memegang referensi untuk menggunakannya.
     */
    public CustomAuthenticationSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override // ini adalah implementasi overriding method dari interface AuthenticationSuccessHandler
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null && user.getRole() != null) {
            String role = user.getRole();

            logger.info("Login berhasil untuk user: {}", username);
            logger.info("Role: {}", role);

            if ("ADMIN".equalsIgnoreCase(role)) {
                response.sendRedirect("/admin/dashboard");
                return;
            } else if ("USER".equalsIgnoreCase(role)) {
                response.sendRedirect("/user/dashboard");
                return;
            } else {
                logger.warn("Role tidak dikenali: {} - redirect ke halaman utama", role);
            }
        } else {
            logger.warn("User tidak ditemukan atau tidak memiliki role - redirect ke halaman utama");
        }

        // Fallback jika user atau role tidak ditemukan
        response.sendRedirect("/");
    }
}
