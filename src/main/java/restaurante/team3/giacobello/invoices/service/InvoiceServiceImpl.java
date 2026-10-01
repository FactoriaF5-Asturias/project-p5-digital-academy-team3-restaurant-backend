package restaurante.team3.giacobello.invoices.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
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
        LocalDate quarterStart = date.with(IsoFields.DAY_OF_QUARTER, 1);
        LocalDate quarterEnd = date.with(
                IsoFields.DAY_OF_QUARTER,
                date.range(IsoFields.DAY_OF_QUARTER).getMaximum());

        return new SalesTotalsDTOResponse(
                sumSalesBetween(date, date),
                sumSalesBetween(
                        date.with(TemporalAdjusters.firstDayOfMonth()),
                        date.with(TemporalAdjusters.lastDayOfMonth())),
                sumSalesBetween(quarterStart, quarterEnd),
                sumSalesBetween(
                        date.with(TemporalAdjusters.firstDayOfYear()),
                        date.with(TemporalAdjusters.lastDayOfYear())));
    }

    private BigDecimal sumSalesBetween(LocalDate from, LocalDate to) {
        return invoiceRepository.sumTotalAmountByIssuedAtBetween(
                from.atStartOfDay(),
                to.atTime(LocalTime.MAX));
    }
}
