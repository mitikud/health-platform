package com.medical.reportservice.service;

import com.medical.reportservice.dto.ReportRequest;
import lombok.var;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ReportService {

    public byte[] generatePdf(ReportRequest req) {
        try (var baos = new ByteArrayOutputStream()) {
            var document = new com.lowagie.text.Document();
            var writer = com.lowagie.text.pdf.PdfWriter.getInstance(document, baos);
            document.open();

            var title = new com.lowagie.text.Paragraph("Health Summary");
            title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(title);

            document.add(new com.lowagie.text.Paragraph("\nUser: " + req.getUserId()));
            document.add(new com.lowagie.text.Paragraph("Language: " + req.getLanguage()));
            if (req.getDiagnosis() != null)
                document.add(new com.lowagie.text.Paragraph("Diagnosis: " + req.getDiagnosis()));

            if (req.getMedications() != null && !req.getMedications().isEmpty()) {
                document.add(new com.lowagie.text.Paragraph("\nMedications:"));
                var list = new com.lowagie.text.List(false, 10);
                for (var m : req.getMedications()) list.add(new com.lowagie.text.ListItem(m));
                document.add(list);
            }

            if (req.getNotes() != null)
                document.add(new com.lowagie.text.Paragraph("\nNotes: " + req.getNotes()));

            document.add(new com.lowagie.text.Paragraph(
                    "\nDisclaimer: Educational only. Requires clinician review."));
            document.close();
            writer.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }
}

