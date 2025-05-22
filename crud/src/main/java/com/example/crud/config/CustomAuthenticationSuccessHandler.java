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

        User user = userRepository.findByUsername(username).orElse(null);
        System.out.println(user.getRole());
        
        if (user != null && user.getRole() != null) {
            String role = user.getRole().toUpperCase();
            if (role.equals("ADMIN") || role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin_mahasiswa");
                return;
            }
        }


        response.sendRedirect("/mahasiswa");
    }

}
