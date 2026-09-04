package health_tracker.demo.controller;

import health_tracker.demo.model.User;
import health_tracker.demo.repository.UserRepository;
import health_tracker.demo.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class PasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordController(
            UserRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // 1️⃣ Show forgot password page
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    // 2️⃣ Send reset link
    @PostMapping("/forgot-password")
    public String sendResetLink(
            @RequestParam String email,
            Model model) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            model.addAttribute("error", "Email not found");
            return "forgot-password";
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        String resetLink =
                "http://localhost:8080/reset-password?token=" + token;

        emailService.sendSimpleMail(
                user.getEmail(),
                "Reset Your Password",
                "Hi " + user.getName() +
                        ",\n\nClick the link below to reset your password:\n"
                        + resetLink +
                        "\n\nIf you did not request this, ignore this email."
        );

        model.addAttribute("message", "Reset link sent to your email");
        return "forgot-password";
    }

    // 3️⃣ Open reset page
    @GetMapping("/reset-password")
    public String resetPasswordPage(
            @RequestParam String token,
            Model model) {

        User user = userRepository.findByResetToken(token).orElse(null);

        if (user == null) {
            model.addAttribute("error", "Invalid or expired token");
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // 4️⃣ Save new password
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String password) {

        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        user.setPassword(passwordEncoder.encode(password));
        user.setResetToken(null);
        userRepository.save(user);

        return "redirect:/login?resetSuccess";
    }
}
