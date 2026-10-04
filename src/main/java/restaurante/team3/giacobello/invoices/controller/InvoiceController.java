package restaurante.team3.giacobello.invoices.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.invoices.dto.InvoiceDTOResponse;
import restaurante.team3.giacobello.invoices.dto.SalesTotalsDTOResponse;
import restaurante.team3.giacobello.invoices.service.InvoicePdfService;
import restaurante.team3.giacobello.invoices.service.InvoiceService;
import restaurante.team3.giacobello.invoices.service.ReportPeriod;
import restaurante.team3.giacobello.invoices.service.SalesReportPdfService;

@RestController
@RequestMapping("${api-endpoint}")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final SalesReportPdfService salesReportPdfService;
    private final InvoicePdfService invoicePdfService;

    public InvoiceController(
            InvoiceService invoiceService,
            SalesReportPdfService salesReportPdfService,
            InvoicePdfService invoicePdfService) {
        this.invoiceService = invoiceService;
        this.salesReportPdfService = salesReportPdfService;
        this.invoicePdfService = invoicePdfService;
    }

    @GetMapping("/orders/{id}/invoice")
    public ResponseEntity<InvoiceDTOResponse> findByOrderId(
            @PathVariable("id") Integer orderId) {
        return ResponseEntity.ok(
                invoiceService.findByOrderId(orderId));
    }

    @GetMapping("/orders/{id}/invoice/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(
            @PathVariable("id") Integer orderId) {
        byte[] pdf = invoicePdfService.generate(orderId);
        String fileName = "factura-pedido-" + orderId + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fileName).build().toString())
                .body(pdf);
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

    @GetMapping("/invoices/totals")
    public ResponseEntity<SalesTotalsDTOResponse> findSalesTotals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate referenceDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(invoiceService.findSalesTotals(referenceDate));
    }

    @GetMapping("/invoices/report/pdf")
    public ResponseEntity<byte[]> downloadSalesReport(
            @RequestParam ReportPeriod period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate referenceDate = date != null ? date : LocalDate.now();
        LocalDate from = period.from(referenceDate);
        LocalDate to = period.to(referenceDate);
        String fileName = "ventas-" + from + "_" + to + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fileName).build().toString())
                .body(salesReportPdfService.generate(from, to));
    }
}
