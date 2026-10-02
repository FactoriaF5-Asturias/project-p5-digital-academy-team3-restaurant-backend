package restaurante.team3.giacobello.invoices.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.invoices.dto.SalesReportArchiveDTOResponse;
import restaurante.team3.giacobello.invoices.service.SalesReportArchiveJob;

@RestController
@RequestMapping("${api-endpoint}/invoices/report/archive")
public class SalesReportArchiveController {

    private final ObjectProvider<SalesReportArchiveJob> salesReportArchiveJob;

    public SalesReportArchiveController(ObjectProvider<SalesReportArchiveJob> salesReportArchiveJob) {
        this.salesReportArchiveJob = salesReportArchiveJob;
    }

    @PostMapping
    public ResponseEntity<SalesReportArchiveDTOResponse> archive(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        SalesReportArchiveJob job = salesReportArchiveJob.getIfAvailable();
        if (job == null) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Supabase no está configurado: faltan SUPABASE_URL y SUPABASE_SERVICE_KEY");
        }

        LocalDate day = date != null ? date : LocalDate.now().minusDays(1);
        try {
            return ResponseEntity.ok(new SalesReportArchiveDTOResponse(day, job.archive(day)));
        } catch (RestClientException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo subir el resumen de ventas a Supabase",
                    e);
        }
    }
}
