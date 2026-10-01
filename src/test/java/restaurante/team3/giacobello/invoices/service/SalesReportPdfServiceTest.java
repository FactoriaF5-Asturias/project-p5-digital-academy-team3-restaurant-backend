package restaurante.team3.giacobello.invoices.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;

@ExtendWith(MockitoExtension.class)
class SalesReportPdfServiceTest {

    private static final LocalDate FROM = LocalDate.of(2026, 9, 1);
    private static final LocalDate TO = LocalDate.of(2026, 9, 30);

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private SalesReportPdfService salesReportPdfService;

    @Test
    void shouldIncludeRestaurantNameAndPeriodInHeader() throws IOException {
        when(invoiceService.findByDateRange(FROM, TO)).thenReturn(List.of());

        String text = extractText(salesReportPdfService.generate(FROM, TO));

        assertTrue(text.contains("Giacobello"));
        assertTrue(text.contains("Resumen de ventas"));
        assertTrue(text.contains("01/09/2026 - 30/09/2026"));
    }

    @Test
    void shouldListEveryInvoiceOfThePeriod() throws IOException {
        when(invoiceService.findByDateRange(FROM, TO)).thenReturn(List.of(
                invoice(7, "INV-7", "12.50", LocalDateTime.of(2026, 9, 3, 13, 15)),
                invoice(9, "INV-9", "17.50", LocalDateTime.of(2026, 9, 20, 21, 40))));

        String text = extractText(salesReportPdfService.generate(FROM, TO));

        assertTrue(text.contains("INV-7"));
        assertTrue(text.contains("03/09/2026 13:15"));
        assertTrue(text.contains("12,50 €"));
        assertTrue(text.contains("INV-9"));
        assertTrue(text.contains("17,50 €"));
    }

    @Test
    void shouldShowOrderCountTotalAndAverageTicket() throws IOException {
        when(invoiceService.findByDateRange(FROM, TO)).thenReturn(List.of(
                invoice(1, "INV-1", "10.00", LocalDateTime.of(2026, 9, 1, 12, 0)),
                invoice(2, "INV-2", "20.00", LocalDateTime.of(2026, 9, 2, 12, 0)),
                invoice(3, "INV-3", "15.01", LocalDateTime.of(2026, 9, 3, 12, 0))));

        String text = extractText(salesReportPdfService.generate(FROM, TO));

        assertTrue(text.contains("Pedidos: 3"));
        assertTrue(text.contains("Total facturado: 45,01 €"));
        assertTrue(text.contains("Ticket medio: 15,00 €"));
    }

    @Test
    void shouldSayThereAreNoSalesWhenPeriodIsEmpty() throws IOException {
        when(invoiceService.findByDateRange(FROM, TO)).thenReturn(List.of());

        String text = extractText(salesReportPdfService.generate(FROM, TO));

        assertTrue(text.contains("Sin ventas en el periodo"));
        assertTrue(text.contains("Pedidos: 0"));
        assertTrue(text.contains("Total facturado: 0,00 €"));
        assertTrue(text.contains("Ticket medio: 0,00 €"));
    }

    @Test
    void shouldShowDashWhenInvoiceHasNoIssueDate() throws IOException {
        when(invoiceService.findByDateRange(FROM, TO)).thenReturn(List.of(
                invoice(4, "INV-4", "8.00", null)));

        String text = extractText(salesReportPdfService.generate(FROM, TO));

        assertTrue(text.contains("INV-4"));
        assertFalse(text.contains("null"));
    }

    @Test
    void shouldAddPagesWhenInvoicesDoNotFitInOne() throws IOException {
        List<InvoiceDTOResponse> invoices = IntStream.rangeClosed(1, 80)
                .mapToObj(id -> invoice(id, "INV-" + id, "5.00", LocalDateTime.of(2026, 9, 10, 12, 0)))
                .toList();
        when(invoiceService.findByDateRange(FROM, TO)).thenReturn(invoices);

        byte[] pdf = salesReportPdfService.generate(FROM, TO);

        try (PDDocument document = Loader.loadPDF(pdf)) {
            assertTrue(document.getNumberOfPages() > 1);
        }
        String text = extractText(pdf);
        assertTrue(text.contains("INV-80"));
        assertTrue(text.contains("Pedidos: 80"));
    }

    @Test
    void shouldKeepAllTotalsTogetherOnTheLastPage() throws IOException {
        for (int count = 20; count <= 60; count++) {
            List<InvoiceDTOResponse> invoices = IntStream.rangeClosed(1, count)
                    .mapToObj(id -> invoice(id, "INV-" + id, "5.00", LocalDateTime.of(2026, 9, 10, 12, 0)))
                    .toList();
            when(invoiceService.findByDateRange(FROM, TO)).thenReturn(invoices);

            String lastPage = extractLastPageText(salesReportPdfService.generate(FROM, TO));

            assertTrue(lastPage.contains("Pedidos: " + count), "Pedidos con " + count + " facturas");
            assertTrue(lastPage.contains("Total facturado:"), "Total con " + count + " facturas");
            assertTrue(lastPage.contains("Ticket medio:"), "Ticket medio con " + count + " facturas");
        }
    }

    @Test
    void shouldFormatAmountsWithThousandsSeparator() throws IOException {
        when(invoiceService.findByDateRange(FROM, TO)).thenReturn(List.of(
                invoice(1, "INV-1", "1234.56", LocalDateTime.of(2026, 9, 1, 12, 0)),
                invoice(2, "INV-2", "2000.00", LocalDateTime.of(2026, 9, 2, 12, 0))));

        String text = extractText(salesReportPdfService.generate(FROM, TO));

        assertTrue(text.contains("1.234,56 €"));
        assertTrue(text.contains("Total facturado: 3.234,56 €"));
        assertTrue(text.contains("Ticket medio: 1.617,28 €"));
    }

    @Test
    void shouldPropagateBadRequestWhenDatesAreInverted() {
        when(invoiceService.findByDateRange(TO, FROM)).thenThrow(
                new ResponseStatusException(HttpStatus.BAD_REQUEST));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> salesReportPdfService.generate(TO, FROM));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    private InvoiceDTOResponse invoice(int orderId, String number, String amount, LocalDateTime issuedAt) {
        return new InvoiceDTOResponse(orderId, orderId, number, new BigDecimal(amount), issuedAt);
    }

    private String extractLastPageText(byte[] pdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(document.getNumberOfPages());
            return stripper.getText(document);
        }
    }

    private String extractText(byte[] pdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            return new PDFTextStripper().getText(document);
        }
    }
}
