package com.example.crud.config;

import com.example.crud.Model.User;
import com.example.crud.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public CustomAuthenticationSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        // Ambil user dari database
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect("/admin/dashboard");
        } else {
            response.sendRedirect("/mahasiswa");
        }
    }
}
