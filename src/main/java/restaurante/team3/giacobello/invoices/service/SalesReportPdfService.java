package restaurante.team3.giacobello.invoices.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;

@Service
public class SalesReportPdfService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Locale SPAIN = Locale.of("es", "ES");
    private static final List<String> TABLE_HEADER = List.of("Factura", "Pedido", "Fecha", "Importe");

    private final InvoiceService invoiceService;

    public SalesReportPdfService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public byte[] generate(LocalDate from, LocalDate to) {

        List<InvoiceDTOResponse> invoices = invoiceService.findByDateRange(from, to);
        PDFont regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDFont bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            try (PageWriter writer = new PageWriter(document, bold)) {
                writer.write(bold, 18, "Giacobello - Resumen de ventas");
                writer.write(bold, 18, from.format(DATE_FORMAT) + " - " + to.format(DATE_FORMAT));
                writer.skipLine();
                if (invoices.isEmpty()) {
                    writer.write(regular, 11, "Sin ventas en el periodo");
                } else {
                    writer.startTable(TABLE_HEADER);
                    for (InvoiceDTOResponse invoice : invoices) {
                        writer.writeRow(regular, invoiceCells(invoice));
                    }
                    writer.endTable();
                }
                writer.skipLine();
                List<String> totals = totalsLines(invoices);
                writer.keepTogether(totals.size());
                for (String line : totals) {
                    writer.write(bold, 11, line);
                }
            }
            document.save(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el PDF", e);
        }
    }

    private List<String> totalsLines(List<InvoiceDTOResponse> invoices) {
        BigDecimal total = invoices.stream()
                .map(InvoiceDTOResponse::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageTicket = invoices.isEmpty()
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(invoices.size()), 2, RoundingMode.HALF_UP);

        return List.of(
                "Pedidos: " + invoices.size(),
                "Total facturado: " + formatAmount(total),
                "Ticket medio: " + formatAmount(averageTicket));
    }

    private List<String> invoiceCells(InvoiceDTOResponse invoice) {
        return List.of(
                invoice.invoiceNumber(),
                String.valueOf(invoice.orderId()),
                formatDateTime(invoice.issuedAt()),
                formatAmount(invoice.totalAmount()));
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMAT) : "-";
    }

    private String formatAmount(BigDecimal amount) {
        return String.format(SPAIN, "%,.2f €", amount);
    }

    private static final class PageWriter implements AutoCloseable {

        private static final float MARGIN = 50;
        private static final float RIGHT_EDGE = 545;
        private static final float LINE_HEIGHT = 22;
        private static final float TABLE_FONT_SIZE = 11;
        private static final float[] COLUMN_X = {50, 170, 260};

        private final PDDocument document;
        private final PDFont headerFont;
        private PDPageContentStream content;
        private List<String> tableHeader = List.of();
        private float y;

        private PageWriter(PDDocument document, PDFont headerFont) throws IOException {
            this.document = document;
            this.headerFont = headerFont;
            newPage();
        }

        private void write(PDFont font, float size, String text) throws IOException {
            if (y < MARGIN) {
                newPage();
            }
            showText(font, size, MARGIN, text);
            y -= LINE_HEIGHT;
        }

        private void startTable(List<String> header) throws IOException {
            tableHeader = header;
            writeHeader();
        }

        private void endTable() {
            tableHeader = List.of();
        }

        private void writeRow(PDFont font, List<String> cells) throws IOException {
            if (y < MARGIN) {
                newPage();
            }
            for (int i = 0; i < COLUMN_X.length; i++) {
                showText(font, TABLE_FONT_SIZE, COLUMN_X[i], cells.get(i));
            }
            String amount = cells.get(COLUMN_X.length);
            float amountWidth = font.getStringWidth(amount) / 1000 * TABLE_FONT_SIZE;
            showText(font, TABLE_FONT_SIZE, RIGHT_EDGE - amountWidth, amount);
            y -= LINE_HEIGHT;
        }

        private void writeHeader() throws IOException {
            writeRow(headerFont, tableHeader);
            float lineY = y + LINE_HEIGHT - 7;
            content.moveTo(MARGIN, lineY);
            content.lineTo(RIGHT_EDGE, lineY);
            content.setLineWidth(0.5f);
            content.stroke();
        }

        private void showText(PDFont font, float size, float x, String text) throws IOException {
            content.beginText();
            content.setFont(font, size);
            content.newLineAtOffset(x, y);
            content.showText(text);
            content.endText();
        }

        private void keepTogether(int lines) throws IOException {
            if (y - (lines - 1) * LINE_HEIGHT < MARGIN) {
                newPage();
            }
        }

        private void skipLine() {
            y -= LINE_HEIGHT;
        }

        private void newPage() throws IOException {
            if (content != null) {
                content.close();
            }
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            content = new PDPageContentStream(document, page);
            y = page.getMediaBox().getHeight() - MARGIN;
            if (!tableHeader.isEmpty()) {
                writeHeader();
            }
        }

        @Override
        public void close() throws IOException {
            content.close();
        }
    }
}
