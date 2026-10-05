package restaurante.team3.giacobello.product.controller;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import restaurante.team3.giacobello.infrastructure.IntegrationTest;
import restaurante.team3.giacobello.product.entity.ProductEntity;
import restaurante.team3.giacobello.product.repository.ProductRepository;

class ProductControllerIntegrationTest extends IntegrationTest {

    private static final String SUPABASE_IMAGES =
            "https://humjxfnaqjxirkknngvf.supabase.co/storage/v1/object/public/images/";


    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAllReturnsOkAndListOfProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products")
            .header("Authorization", "Bearer " + tokenForRole("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void findAllReturnsProductsSortedByName() throws Exception {
        mockMvc.perform(get("/api/v1/products")
            .header("Authorization", "Bearer " + tokenForRole("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Acqua di localhost"))
                .andExpect(jsonPath("$[1].name").value("AI a la Carte"))
                .andExpect(jsonPath("$[2].name").value("Arancini Gitilini"));

    }

    @Test
    void findAllReturnsAllFieldsOfProduct() throws Exception {
        mockMvc.perform(get("/api/v1/products")
            .header("Authorization", "Bearer " + tokenForRole("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Acqua di localhost"))
                .andExpect(jsonPath("$[0].description")
                        .value("Lágrimas acumuladas de alumnos de Giaco que no pudieron terminar los ejercicios"))
                .andExpect(jsonPath("$[0].category").value("Bebidas"))
                .andExpect(jsonPath("$[0].price").value(60.0))
                .andExpect(jsonPath("$[0].status").value(true))
                .andExpect(jsonPath("$[0].imageUrl").value(SUPABASE_IMAGES + "Acqua_di_localhost.jpg"));
    }

    @Test
    void findAllServesEveryProductImageFromSupabase() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].imageUrl", everyItem(startsWith(SUPABASE_IMAGES))));
    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Transactional
    void findAllReturnsEmptyListWhenNoProducts() throws Exception {
        productRepository.deleteAll();

        mockMvc.perform(get("/api/v1/products")
            .header("Authorization", "Bearer " + tokenForRole("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    void findAllExcludesUnavailableProducts() throws Exception {
        ProductEntity acqua = productRepository.findById(10).orElseThrow();
        acqua.setStatus(false);
        productRepository.saveAndFlush(acqua);

        mockMvc.perform(get("/api/v1/products")
            .header("Authorization", "Bearer " + tokenForRole("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(11))
                .andExpect(jsonPath("$[0].name").value("AI a la Carte"));
    }

    @Test
    void adminProductsRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/admin/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findByIdReturnsNotFoundWhenProductDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/products/100")
            .header("Authorization", "Bearer " + tokenForRole("CLIENT")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found with id : 100"));
    }
}
