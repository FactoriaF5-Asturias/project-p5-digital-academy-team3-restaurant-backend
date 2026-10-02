package restaurante.team3.giacobello.invoices.service;

import java.time.Clock;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;

import restaurante.team3.giacobello.storage.SupabaseStorageClient;

public class SalesReportArchiveJob {

    private static final Logger log = LoggerFactory.getLogger(SalesReportArchiveJob.class);

    private final SalesReportPdfService salesReportPdfService;
    private final SupabaseStorageClient storageClient;
    private final Clock clock;

    public SalesReportArchiveJob(
            SalesReportPdfService salesReportPdfService,
            SupabaseStorageClient storageClient,
            Clock clock) {
        this.salesReportPdfService = salesReportPdfService;
        this.storageClient = storageClient;
        this.clock = clock;
    }

    @Scheduled(cron = "${sales-report.archive.cron:0 5 0 * * *}", zone = "Europe/Madrid")
    public void archiveYesterday() {
        LocalDate yesterday = LocalDate.now(clock).minusDays(1);
        try {
            String path = archive(yesterday);
            log.info("Resumen de ventas del {} subido a {}", yesterday, path);
        } catch (RuntimeException e) {
            log.error("No se pudo archivar el resumen de ventas del {}", yesterday, e);
        }
    }

    public String archive(LocalDate day) {
        byte[] pdf = salesReportPdfService.generate(day, day);
        String path = "daily/sales-report-" + day + ".pdf";
        storageClient.upload(path, pdf, MediaType.APPLICATION_PDF);
        return path;
    }
}
