package com.example.crud.config;

import com.example.crud.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Agregasi: UserRepository disuntikkan dari luar ke kelas konfigurasi ini
    private final UserRepository userRepository;

    // Konstruktor menerima UserRepository, contoh agregasi (dependency injection)
    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Bean untuk mengatur aturan otorisasi dan autentikasi pada setiap endpoint
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler successHandler) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/", "/login", "/register", "/qr-payment", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)  // ini implementasi menggunakan bean AuthenticationSuccessHandler (agregasi objek)
                .failureHandler(authenticationFailureHandler()) // ini implementasi custom failure handler
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // Bean untuk menghilangkan prefix default "ROLE_" pada otorisasi Spring Security
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // hapus prefix "ROLE_"
    }

    // Bean untuk encoder password menggunakan BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean untuk mengambil AuthenticationManager dari konfigurasi Spring
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Bean untuk menangani redirect setelah login berhasil
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        // Contoh pewarisan (implements) interface AuthenticationSuccessHandler,
        // objek CustomAuthenticationSuccessHandler dibuat di sini.
        return new CustomAuthenticationSuccessHandler();
    }

    // Bean untuk menangani kegagalan login dengan pengecekan status user
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String username = request.getParameter("username");
            final String[] errorMessage = {"Username atau password salah"};

            if (exception instanceof DisabledException) {
                errorMessage[0] = exception.getMessage();
            } else {
                // Menggunakan agregasi: mengakses userRepository untuk mendapatkan data user
                userRepository.findByUsername(username).ifPresent(user -> {
                    String status = user.getStatus();
                    if ("INACTIVE".equalsIgnoreCase(status)) {
                        errorMessage[0] = "Akun sudah tidak aktif";
                    } else if ("PENDING".equalsIgnoreCase(status)) {
                        errorMessage[0] = "Akun belum aktif, silakan tunggu aktivasi";
                    }
                });
            }

            String encodedMessage = URLEncoder.encode(errorMessage[0], StandardCharsets.UTF_8);
            response.sendRedirect("/login?error=true&message=" + encodedMessage);
        };
    }

    // Bean untuk memuat data user dari database serta melakukan validasi status user
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(user -> {
                    if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
                        throw new DisabledException("Akun sudah tidak aktif");
                    } else if ("PENDING".equalsIgnoreCase(user.getStatus())) {
                        throw new DisabledException("Akun belum aktif, silakan tunggu aktivasi");
                    }

                    return org.springframework.security.core.userdetails.User.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .authorities(user.getRole())
                            .accountExpired(false)
                            .accountLocked(false)
                            .credentialsExpired(false)
                            .disabled(false)
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // Kelas internal yang mengimplementasikan interface AuthenticationSuccessHandler
    // ini adalah implementasi inheritance (pewarisan interface)
    public static class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

        @Override // ini adalah implementasi overriding method dari interface AuthenticationSuccessHandler
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws IOException, ServletException {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
            boolean isUser = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("USER"));

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else if (isUser) {
                response.sendRedirect("/home");
            } else {
                response.sendRedirect("/");
            }
        }
    }
}
