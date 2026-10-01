package restaurante.team3.giacobello.invoices.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;

@Service
public class SalesReportPdfService {

    private final InvoiceService invoiceService;

    public SalesReportPdfService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public byte[] generate(LocalDate from, LocalDate to) {

        List<InvoiceDTOResponse> invoices = invoiceService.findByDateRange(from, to);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                content.newLineAtOffset(50, 790);
                content.showText("Giacobello - Resumen de ventas");
                content.newLineAtOffset(0, -25);
                content.showText(from.format(dateFormat) + " - " + to.format(dateFormat));
                content.endText();
            }
            document.addPage(page);
            document.save(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el PDF", e);
        }

    }
}
