package restaurante.team3.giacobello.invoices.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;

import restaurante.team3.giacobello.storage.SupabaseStorageClient;

@ExtendWith(MockitoExtension.class)
class SalesReportArchiveJobTest {

    private static final ZoneId MADRID = ZoneId.of("Europe/Madrid");
    private static final byte[] PDF = { 37, 80, 68, 70 };

    @Mock
    private SalesReportPdfService salesReportPdfService;

    @Mock
    private SupabaseStorageClient storageClient;

    private SalesReportArchiveJob job;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                ZonedDateTime.of(2026, 10, 2, 0, 5, 0, 0, MADRID).toInstant(),
                MADRID);
        job = new SalesReportArchiveJob(salesReportPdfService, storageClient, clock);
    }

    @Test
    void archiveYesterdayUploadsThePdfOfThePreviousDay() {
        LocalDate yesterday = LocalDate.of(2026, 10, 1);
        when(salesReportPdfService.generate(yesterday, yesterday)).thenReturn(PDF);

        job.archiveYesterday();

        verify(storageClient).upload(
                "daily/sales-report-2026-10-01.pdf",
                PDF,
                MediaType.APPLICATION_PDF);
    }

    @Test
    void archiveReturnsThePathOfTheUploadedReport() {
        LocalDate day = LocalDate.of(2026, 9, 30);
        when(salesReportPdfService.generate(day, day)).thenReturn(PDF);

        String path = job.archive(day);

        assertEquals("daily/sales-report-2026-09-30.pdf", path);
    }

    @Test
    void archiveYesterdayDoesNotThrowWhenTheUploadFails() {
        when(salesReportPdfService.generate(any(), any())).thenReturn(PDF);
        doThrow(new RestClientException("Supabase down"))
                .when(storageClient).upload(anyString(), any(), any());

        assertDoesNotThrow(() -> job.archiveYesterday());
    }

    @Test
    void archiveYesterdayDoesNotUploadWhenThePdfCannotBeGenerated() {
        when(salesReportPdfService.generate(any(), any()))
                .thenThrow(new UncheckedIOException("No se pudo generar el PDF", new IOException()));

        assertDoesNotThrow(() -> job.archiveYesterday());

        verifyNoInteractions(storageClient);
    }
}
