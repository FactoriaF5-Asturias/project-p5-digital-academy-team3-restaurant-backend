package restaurante.team3.giacobello.invoices.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import restaurante.team3.giacobello.infrastructure.IntegrationTest;

class InvoiceControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminCanFindAllInvoices() throws Exception {
        mockMvc.perform(get("/api/v1/invoices")
            .header("Authorization", "Bearer " + tokenForRole("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void clientCannotFindAllInvoices() throws Exception {
        mockMvc.perform(get("/api/v1/invoices")
                .header("Authorization", "Bearer " + tokenForRole("CLIENT")))
                .andExpect(status().isForbidden());
    }
}
