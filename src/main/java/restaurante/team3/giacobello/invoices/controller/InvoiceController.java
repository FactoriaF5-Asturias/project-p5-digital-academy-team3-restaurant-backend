package restaurante.team3.giacobello.invoices.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;
import restaurante.team3.giacobello.invoices.service.InvoiceService;

@RestController
@RequestMapping("${api-endpoint}")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/orders/{id}/invoice")
    public ResponseEntity<InvoiceDTOResponse> findByOrderId(
            @PathVariable("id") Integer orderId) {
        return ResponseEntity.ok(
                invoiceService.findByOrderId(orderId));
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceDTOResponse>> findAll() {
        return ResponseEntity.ok(invoiceService.findAll());
    }
}
