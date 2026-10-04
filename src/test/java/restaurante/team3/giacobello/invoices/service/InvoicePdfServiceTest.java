package restaurante.team3.giacobello.invoices.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
import restaurante.team3.giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.giacobello.orders.dto.OrderItemDTOResponse;
import restaurante.team3.giacobello.orders.service.OrderService;

@ExtendWith(MockitoExtension.class)
class InvoicePdfServiceTest {

    private static final int ORDER_ID = 12;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private InvoicePdfService invoicePdfService;

    @Test
    void shouldIncludeInvoiceNumberOrderDateAndPaymentMethod() throws IOException {
        givenPaidOrder();

        String text = extractText(invoicePdfService.generate(ORDER_ID));

        assertTrue(text.contains("Giacobello"));
        assertTrue(text.contains("Factura: INV-12"));
        assertTrue(text.contains("Pedido: 12"));
        assertTrue(text.contains("Fecha: 28/09/2026 13:15"));
        assertTrue(text.contains("Método de pago: CASH"));
    }

    @Test
    void shouldListEveryLineOfTheOrder() throws IOException {
        givenPaidOrder();

        String text = extractText(invoicePdfService.generate(ORDER_ID));

        assertTrue(text.contains("Croquetas de jamón"));
        assertTrue(text.contains("4,50 €"));
        assertTrue(text.contains("9,00 €"));
        assertTrue(text.contains("Tarta de queso"));
        assertTrue(text.contains("6,00 €"));
    }

    @Test
    void shouldShowTheInvoiceTotal() throws IOException {
        givenPaidOrder();

        String text = extractText(invoicePdfService.generate(ORDER_ID));

        assertTrue(text.contains("Total: 15,00 €"));
    }

    @Test
    void shouldPropagateNotFoundWhenOrderHasNoInvoice() {
        when(invoiceService.findByOrderId(ORDER_ID)).thenThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> invoicePdfService.generate(ORDER_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(orderService);
    }

    private void givenPaidOrder() {
        LocalDateTime paidAt = LocalDateTime.of(2026, 9, 28, 13, 15);
        when(invoiceService.findByOrderId(ORDER_ID)).thenReturn(new InvoiceDTOResponse(
                3, ORDER_ID, "INV-12", new BigDecimal("15.00"), paidAt));
        when(orderService.findById(ORDER_ID)).thenReturn(new OrderDTOResponse(
                ORDER_ID, 2, "DINE IN", "CASH", "COMPLETED",
                new BigDecimal("15.00"),
                LocalDateTime.of(2026, 9, 28, 12, 50),
                paidAt,
                List.of(
                        new OrderItemDTOResponse(1, 4, "Croquetas de jamón", 2,
                                new BigDecimal("4.50"), new BigDecimal("9.00")),
                        new OrderItemDTOResponse(2, 9, "Tarta de queso", 1,
                                new BigDecimal("6.00"), new BigDecimal("6.00")))));
    }

    private String extractText(byte[] pdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            return new PDFTextStripper().getText(document);
        }
    }
}
