package restaurante.team3.Giacobello.invoices.service;

import restaurante.team3.Giacobello.invoices.dto.InvoiceDTOResponse;

public interface InvoiceService {
    InvoiceDTOResponse findByOrderId(Integer orderId);
}
