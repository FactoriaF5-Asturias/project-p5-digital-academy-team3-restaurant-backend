package restaurante.team3.giacobello.invoices.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;

import restaurante.team3.giacobello.infrastructure.IntegrationTest;
import restaurante.team3.giacobello.invoices.service.SalesReportArchiveJob;

@ActiveProfiles({ "test", "dev" })
class SalesReportArchiveControllerIntegrationTest extends IntegrationTest {

    private static final String ARCHIVE_URL = "/api/v1/invoices/report/archive";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalesReportArchiveJob salesReportArchiveJob;

    @Test
    void archiveUploadsTheReportOfTheGivenDay() throws Exception {
        LocalDate day = LocalDate.of(2026, 10, 1);
        when(salesReportArchiveJob.archive(day)).thenReturn("daily/sales-report-2026-10-01.pdf");

        mockMvc.perform(post(ARCHIVE_URL)
                .servletPath(ARCHIVE_URL)
                .header("Authorization", "Bearer " + tokenForRole("ADMIN"))
                .param("date", "2026-10-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-10-01"))
                .andExpect(jsonPath("$.path").value("daily/sales-report-2026-10-01.pdf"));

        verify(salesReportArchiveJob).archive(day);
    }

    @Test
    void archiveUsesYesterdayWhenNoDateIsGiven() throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        when(salesReportArchiveJob.archive(yesterday)).thenReturn("daily/sales-report-" + yesterday + ".pdf");

        mockMvc.perform(post(ARCHIVE_URL)
                .servletPath(ARCHIVE_URL)
                .header("Authorization", "Bearer " + tokenForRole("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(yesterday.toString()));

        verify(salesReportArchiveJob).archive(yesterday);
    }

    @Test
    void archiveReturnsBadGatewayWhenTheUploadFails() throws Exception {
        LocalDate day = LocalDate.of(2026, 10, 1);
        when(salesReportArchiveJob.archive(day)).thenThrow(new RestClientException("Supabase down"));

        mockMvc.perform(post(ARCHIVE_URL)
                .servletPath(ARCHIVE_URL)
                .header("Authorization", "Bearer " + tokenForRole("ADMIN"))
                .param("date", "2026-10-01"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void archiveReturnsBadRequestWhenTheDateIsNotValid() throws Exception {
        mockMvc.perform(post(ARCHIVE_URL)
                .servletPath(ARCHIVE_URL)
                .header("Authorization", "Bearer " + tokenForRole("ADMIN"))
                .param("date", "01-10-2026"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void archiveReturnsUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(post(ARCHIVE_URL)
                .servletPath(ARCHIVE_URL)
                .param("date", "2026-10-01"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void archiveReturnsForbiddenForKitchenRole() throws Exception {
        mockMvc.perform(post(ARCHIVE_URL)
                .servletPath(ARCHIVE_URL)
                .header("Authorization", "Bearer " + tokenForRole("KITCHEN"))
                .param("date", "2026-10-01"))
                .andExpect(status().isForbidden());
    }
}
