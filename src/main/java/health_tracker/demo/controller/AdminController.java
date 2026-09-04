package health_tracker.demo.controller;

import health_tracker.demo.dto.ChartDataDto;
import health_tracker.demo.dto.WeeklySummary;
import health_tracker.demo.model.User;
import health_tracker.demo.repository.UserRepository;
import health_tracker.demo.service.HealthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final HealthService healthService;

    // ✅ Constructor injection (IMPORTANT)
    public AdminController(UserRepository userRepository,
                           HealthService healthService) {
        this.userRepository = userRepository;
        this.healthService = healthService;
    }

    // ================= ADMIN DASHBOARD =================
    @GetMapping
    public String adminDashboard(Model model, Principal principal) {

        String adminEmail = principal.getName();

        // ❌ Exclude admin from users list
        List<User> users = userRepository.findAll()
                .stream()
                .filter(u -> !u.getEmail().equals(adminEmail))
                .toList();

        model.addAttribute("users", users);
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("activeUsers",
                users.stream().filter(User::isEnabled).count());
        model.addAttribute("blockedUsers",
                users.stream().filter(u -> !u.isEnabled()).count());

        return "admin-dashboard";
    }

    // ================= VIEW USER DETAILS =================
    @GetMapping("/user/{id}")
    public String viewUserDetails(@PathVariable Long id, Model model) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WeeklySummary weekly = healthService.getWeeklySummary(user);
        ChartDataDto chart = healthService.getLast7DaysChart(user);

        // ✅ Safety: prevent Thymeleaf crashes
        if (chart == null) {
            chart = new ChartDataDto(
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of()
            );
        }

        model.addAttribute("user", user);
        model.addAttribute("weekly", weekly);
        model.addAttribute("chart", chart);

        return "admin-user-details";
    }

    // ================= ENABLE USER =================
    @PostMapping("/enable/{id}")
    public String enableUser(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setEnabled(true);
            userRepository.save(user);
        });
        return "redirect:/admin";
    }

    // ================= DISABLE USER =================
    @PostMapping("/disable/{id}")
    public String disableUser(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setEnabled(false);
            userRepository.save(user);
        });
        return "redirect:/admin";
    }

    // ================= DELETE USER =================
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin";
    }
}
