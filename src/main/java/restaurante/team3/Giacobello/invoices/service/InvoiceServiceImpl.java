package restaurante.team3.Giacobello.invoices.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.Giacobello.invoices.dto.InvoiceDTOResponse;
import restaurante.team3.Giacobello.invoices.entity.InvoiceEntity;
import restaurante.team3.Giacobello.invoices.mappers.InvoiceMapper;
import restaurante.team3.Giacobello.invoices.repository.InvoiceRepository;

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
                        "No existe una factura para el pedido " + orderId
                ));

        return invoiceMapper.toResponse(invoice);
    }
}
