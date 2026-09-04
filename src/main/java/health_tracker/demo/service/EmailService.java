package health_tracker.demo.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 🔹 SIMPLE TEXT MAIL (Forgot Password)
    public void sendSimpleMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    // 🔹 PDF MAIL (Already used earlier)
    public void sendPdf(
            String toEmail,
            String subject,
            String text,
            byte[] pdfBytes
    ) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(text);
        helper.addAttachment(
                "health-report.pdf",
                () -> new java.io.ByteArrayInputStream(pdfBytes)
        );

        mailSender.send(message);
    }
}
