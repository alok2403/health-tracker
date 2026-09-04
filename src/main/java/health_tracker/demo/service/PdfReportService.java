package health_tracker.demo.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import health_tracker.demo.model.HealthRecord;
import health_tracker.demo.model.User;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfReportService {

    public byte[] generateWeeklyReport(
            User user,
            List<HealthRecord> records
    ) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, out);

        document.open();

        document.add(new Paragraph("Health Report"));
        document.add(new Paragraph("User: " + user.getName()));
        document.add(new Paragraph(" "));

        for (HealthRecord r : records) {
            document.add(new Paragraph(
                    r.getDate()
                            + " | Steps: " + r.getSteps()
                            + " | Calories: " + r.getCalories()
                            + " | Water: " + r.getWaterIntake()
            ));
        }

        document.close();

        return out.toByteArray(); // ✅ byte[]
    }
}
