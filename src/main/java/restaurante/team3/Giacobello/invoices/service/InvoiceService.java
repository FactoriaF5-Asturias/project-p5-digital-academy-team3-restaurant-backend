package restaurante.team3.giacobello.invoices.service;

import java.util.List;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;

public interface InvoiceService {
    InvoiceDTOResponse findByOrderId(Integer orderId);

    List<InvoiceDTOResponse> findAll();

}
