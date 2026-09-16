package restaurante.team3.Giacobello.invoices.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.Giacobello.invoices.dto.InvoiceDTOResponse;
import restaurante.team3.Giacobello.invoices.entity.InvoiceEntity;
import restaurante.team3.Giacobello.invoices.mappers.InvoiceMapper;
import restaurante.team3.Giacobello.invoices.repository.InvoiceRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Test
    void shouldReturnInvoiceForOrder() {
        InvoiceEntity invoice = mock(InvoiceEntity.class);
        InvoiceDTOResponse response = new InvoiceDTOResponse(
                1,
                5,
                "TEST-0001",
                new BigDecimal("25.00"),
                LocalDateTime.of(2026, 9, 16, 12, 0));
        when(invoiceRepository.findByOrderId(5))
                .thenReturn(Optional.of(invoice));
        when(invoiceMapper.toResponse(invoice))
                .thenReturn(response);
        InvoiceDTOResponse result = invoiceService.findByOrderId(5);
        assertEquals(response, result);
    }

    @Test
    void shouldReturnNotFoundWhenInvoiceDoesNotExist() {
        when(invoiceRepository.findByOrderId(5))
                .thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> invoiceService.findByOrderId(5));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(invoiceMapper);
    }
}
