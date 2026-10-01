package restaurante.team3.giacobello.invoices.service;

import java.time.LocalDate;
import java.util.List;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;
import restaurante.team3.giacobello.invoices.dto.SalesTotalsDTOResponse;

public interface InvoiceService {
    InvoiceDTOResponse findByOrderId(Integer orderId);

    List<InvoiceDTOResponse> findAll();

    List<InvoiceDTOResponse> findByDateRange(LocalDate from, LocalDate to);

    SalesTotalsDTOResponse findSalesTotals(LocalDate date);

}
