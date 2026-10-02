package restaurante.team3.giacobello.invoices.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import restaurante.team3.giacobello.infrastructure.IntegrationTest;

@ActiveProfiles({ "test", "dev" })
class SalesReportArchiveNotConfiguredIntegrationTest extends IntegrationTest {

    private static final String ARCHIVE_URL = "/api/v1/invoices/report/archive";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void archiveReturnsServiceUnavailableWhenSupabaseIsNotConfigured() throws Exception {
        mockMvc.perform(post(ARCHIVE_URL)
                .servletPath(ARCHIVE_URL)
                .param("date", "2026-10-01"))
                .andExpect(status().isServiceUnavailable());
    }
}
