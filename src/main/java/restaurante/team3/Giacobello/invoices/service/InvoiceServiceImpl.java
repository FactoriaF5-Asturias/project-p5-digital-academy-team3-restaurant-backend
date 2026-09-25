package restaurante.team3.giacobello.invoices.service;

import java.util.List;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;
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
}
