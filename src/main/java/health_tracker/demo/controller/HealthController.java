package health_tracker.demo.controller;

import health_tracker.demo.dto.ChartDataDto;
import health_tracker.demo.dto.WeeklySummary;
import health_tracker.demo.model.HealthRecord;
import health_tracker.demo.model.User;
import health_tracker.demo.repository.UserRepository;
import health_tracker.demo.service.EmailService;
import health_tracker.demo.service.HealthService;
import health_tracker.demo.service.PdfReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class HealthController {

    private final HealthService healthService;
    private final UserRepository userRepository;
    private final PdfReportService pdfReportService;
    private final EmailService emailService;

    public HealthController(
            HealthService healthService,
            UserRepository userRepository,
            PdfReportService pdfReportService,
            EmailService emailService) {

        this.healthService = healthService;
        this.userRepository = userRepository;
        this.pdfReportService = pdfReportService;
        this.emailService = emailService;
    }

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        User user = getLoggedInUser();

        HealthRecord today = healthService.getTodayRecord(user);
        WeeklySummary weekly = healthService.getWeeklySummary(user);
        ChartDataDto chart = healthService.getLast7DaysChart(user);

        model.addAttribute("record", today);
        model.addAttribute("weekly", weekly);
        model.addAttribute("chart", chart);
        model.addAttribute("username", user.getName());

        return "dashboard";
    }

    // ================= CHARTS PAGE =================
    @GetMapping("/charts")
    public String charts(Model model) {
        User user = getLoggedInUser();
        model.addAttribute("chart", healthService.getLast7DaysChart(user));
        return "charts";
    }

    @GetMapping("/profile")
    public String profile(Model model) {

        User user = getLoggedInUser();

        model.addAttribute("user", user);
        return "profile";
    }


    // ================= DOWNLOAD PDF =================
    @GetMapping("/report/pdf")
    public void downloadPdf(HttpServletResponse response) throws Exception {

        User user = getLoggedInUser();
        List<HealthRecord> records = healthService.getLast7Days(user);

        byte[] pdfBytes = pdfReportService.generateWeeklyReport(user, records);

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=health-report.pdf"
        );

        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    // ================= EMAIL PDF (POST ONLY) =================
    @PostMapping("/report/email")
    public String emailPdf(Model model) {

        try {
            User user = getLoggedInUser();

            var records = healthService.getLast7Days(user);
            byte[] pdf = pdfReportService.generateWeeklyReport(user, records);

            emailService.sendPdf(
                    user.getEmail(),
                    "Your Weekly Health Report",
                    "Hi " + user.getName() + ",\n\nPlease find your weekly health report attached.",
                    pdf
            );

            model.addAttribute("emailSuccess", true);

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 THIS WILL SHOW REAL ERROR IN CONSOLE
            model.addAttribute("emailError", "Failed to send email");
        }

        return "redirect:/dashboard";
    }


    @PostMapping("/profile/upload")
    public String uploadProfileImage(@RequestParam("image") MultipartFile file) {

        try {
            User user = getLoggedInUser();

            if (file.isEmpty()) {
                return "redirect:/profile";
            }

            // ✅ ABSOLUTE upload path (IMPORTANT)
            String uploadDir = "C:/health-tracker/uploads/";

            java.io.File dir = new java.io.File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            java.io.File destination = new java.io.File(uploadDir + fileName);

            file.transferTo(destination);

            user.setProfileImage(fileName);
            userRepository.save(user);

        } catch (Exception e) {
            e.printStackTrace(); // now real errors will be visible
        }

        return "redirect:/profile";
    }





    // ================= SAVE DAILY DATA =================
    @PostMapping("/save")
    public String save(@ModelAttribute HealthRecord record) {

        User user = getLoggedInUser();
        healthService.saveOrUpdate(record, user);

        return "redirect:/dashboard";
    }

    // ================= SET DAILY TARGETS =================
    @PostMapping("/set-targets")
    public String setTargets(
            @RequestParam int stepGoal,
            @RequestParam int calorieGoal,
            @RequestParam int waterGoal) {

        User user = getLoggedInUser();
        healthService.updateTargets(user, stepGoal, calorieGoal, waterGoal);

        return "redirect:/dashboard";
    }

    // ================= AUTH UTILITY =================
    private User getLoggedInUser() {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = auth.getPrincipal();
        String email;

        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            email = principal.toString();
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
