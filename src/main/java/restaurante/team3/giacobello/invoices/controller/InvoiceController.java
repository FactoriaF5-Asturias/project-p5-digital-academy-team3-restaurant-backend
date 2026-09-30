package restaurante.team3.giacobello.invoices.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<List<InvoiceDTOResponse>> findAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from == null && to == null) {
            return ResponseEntity.ok(invoiceService.findAll());
        }
        if (from == null || to == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hay que indicar las dos fechas: from y to");
        }
        return ResponseEntity.ok(invoiceService.findByDateRange(from, to));
    }
}
