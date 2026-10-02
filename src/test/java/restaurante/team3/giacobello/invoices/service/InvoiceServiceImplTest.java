package restaurante.team3.giacobello.invoices.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;
import restaurante.team3.giacobello.invoices.dto.SalesTotalsDTOResponse;
import restaurante.team3.giacobello.invoices.entity.InvoiceEntity;
import restaurante.team3.giacobello.invoices.mappers.InvoiceMapper;
import restaurante.team3.giacobello.invoices.repository.InvoiceRepository;
import restaurante.team3.giacobello.invoices.service.InvoiceServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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

    @Test
    void shouldReturnAllInvoicesMappedToResponse() {
        InvoiceEntity invoice = mock(InvoiceEntity.class);
        InvoiceDTOResponse response = new InvoiceDTOResponse(
                1,
                5,
                "INV-5",
                new BigDecimal("25.00"),
                LocalDateTime.of(2026, 9, 16, 12, 0));
        when(invoiceRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(invoice));
        when(invoiceMapper.toResponse(invoice))
                .thenReturn(response);
        List<InvoiceDTOResponse> result = invoiceService.findAll();
        assertEquals(List.of(response), result);
    }

    @Test
    void shouldSearchInvoicesFromStartOfFirstDayToEndOfLastDay() {
        LocalDate from = LocalDate.of(2026, 9, 1);
        LocalDate to = LocalDate.of(2026, 9, 30);
        InvoiceEntity invoice = mock(InvoiceEntity.class);
        InvoiceDTOResponse response = new InvoiceDTOResponse(
                1,
                5,
                "INV-5",
                new BigDecimal("25.00"),
                LocalDateTime.of(2026, 9, 16, 12, 0));
        LocalDateTime start = LocalDateTime.of(2026, 9, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(LocalDate.of(2026, 9, 30), LocalTime.MAX);
        when(invoiceRepository.findByIssuedAtBetweenOrderByIssuedAtDesc(start, end))
                .thenReturn(List.of(invoice));
        when(invoiceMapper.toResponse(invoice))
                .thenReturn(response);
        List<InvoiceDTOResponse> result = invoiceService.findByDateRange(from, to);
        assertEquals(List.of(response), result);
        verify(invoiceRepository).findByIssuedAtBetweenOrderByIssuedAtDesc(start, end);
    }

    @Test
    void shouldReturnBadRequestWhenFromIsAfterTo() {
        LocalDate from = LocalDate.of(2026, 9, 30);
        LocalDate to = LocalDate.of(2026, 9, 1);
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> invoiceService.findByDateRange(from, to));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(invoiceRepository);
    }

    @Test
    void shouldSumSalesAndCountOrdersOfDayMonthQuarterAndYearOfGivenDate() {
        LocalDate date = LocalDate.of(2026, 8, 15);
        stubPeriod(LocalDate.of(2026, 8, 15), LocalDate.of(2026, 8, 15), "20.00", 1);
        stubPeriod(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31), "140.00", 7);
        stubPeriod(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 9, 30), "380.00", 19);
        stubPeriod(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), "1200.00", 60);

        SalesTotalsDTOResponse result = invoiceService.findSalesTotals(date);

        assertEquals(new SalesTotalsDTOResponse(
                new BigDecimal("20.00"),
                new BigDecimal("140.00"),
                new BigDecimal("380.00"),
                new BigDecimal("1200.00"),
                1,
                7,
                19,
                60), result);
    }

    private void stubPeriod(LocalDate from, LocalDate to, String sales, long orders) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX);
        when(invoiceRepository.sumTotalAmountByIssuedAtBetween(start, end))
                .thenReturn(new BigDecimal(sales));
        when(invoiceRepository.countByIssuedAtBetween(start, end))
                .thenReturn(orders);
    }
}
