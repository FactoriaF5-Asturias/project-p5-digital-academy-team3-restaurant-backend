package restaurante.team3.Giacobello.product.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import restaurante.team3.Giacobello.infrastructure.IntegrationTest;
import restaurante.team3.Giacobello.product.repository.ProductRepository;

class ProductControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAllReturnsOkAndListOfProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void findAllReturnsProductsSortedByName() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Acqua di localhost"))
                .andExpect(jsonPath("$[1].name").value("AI a la Carte"))
                .andExpect(jsonPath("$[2].name").value("Arancini Gitilini"));

    }

    @Test
    void findAllReturnsAllFieldsOfProduct() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Acqua di localhost"))
                .andExpect(jsonPath("$[0].description")
                        .value("Lágrimas acumuladas de alumnos de Giaco que no pudieron terminar los ejercicios"))
                .andExpect(jsonPath("$[0].category").value("Bebidas"))
                .andExpect(jsonPath("$[0].price").value(60.0))
                .andExpect(jsonPath("$[0].status").value(true))
                .andExpect(jsonPath("$[0].imageUrl").value("/images/products/Acqua_di_localhost.jpg"));
    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Transactional
    void findAllReturnsEmptyListWhenNoProducts() throws Exception {
        productRepository.deleteAll();

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
