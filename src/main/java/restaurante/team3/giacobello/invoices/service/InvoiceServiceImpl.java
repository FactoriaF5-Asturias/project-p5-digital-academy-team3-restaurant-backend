package restaurante.team3.giacobello.invoices.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;
import restaurante.team3.giacobello.invoices.dto.SalesTotalsDTOResponse;
import restaurante.team3.giacobello.invoices.entity.InvoiceEntity;
import restaurante.team3.giacobello.invoices.mappers.InvoiceMapper;
import restaurante.team3.giacobello.invoices.repository.InvoiceRepository;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceServiceImpl(
            InvoiceRepository invoiceRepository,
            InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDTOResponse findByOrderId(Integer orderId) {
        InvoiceEntity invoice = invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una factura para el pedido " + orderId));

        return invoiceMapper.toResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTOResponse> findAll() {
        return invoiceRepository.findAll(Sort.by(InvoiceEntity::getId).descending())
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTOResponse> findByDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX);

        return invoiceRepository.findByIssuedAtBetweenOrderByIssuedAtDesc(start, end)
                .stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SalesTotalsDTOResponse findSalesTotals(LocalDate date) {
        return new SalesTotalsDTOResponse(
                sumSales(ReportPeriod.DAY, date),
                sumSales(ReportPeriod.MONTH, date),
                sumSales(ReportPeriod.QUARTER, date),
                sumSales(ReportPeriod.YEAR, date),
                countOrders(ReportPeriod.DAY, date),
                countOrders(ReportPeriod.MONTH, date),
                countOrders(ReportPeriod.QUARTER, date),
                countOrders(ReportPeriod.YEAR, date));
    }

    private BigDecimal sumSales(ReportPeriod period, LocalDate date) {
        return invoiceRepository.sumTotalAmountByIssuedAtBetween(
                period.from(date).atStartOfDay(),
                period.to(date).atTime(LocalTime.MAX));
    }

    private long countOrders(ReportPeriod period, LocalDate date) {
        return invoiceRepository.countByIssuedAtBetween(
                period.from(date).atStartOfDay(),
                period.to(date).atTime(LocalTime.MAX));
    }
}
