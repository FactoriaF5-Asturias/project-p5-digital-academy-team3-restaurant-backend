package restaurante.team3.giacobello.exceptions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import restaurante.team3.giacobello.categories.exceptions.CategoryAlreadyExistsException;
import restaurante.team3.giacobello.categories.exceptions.CategoryHasProductsException;
import restaurante.team3.giacobello.categories.exceptions.CategoryNotFoundException;
import restaurante.team3.giacobello.payments.exceptions.PaymentGatewayException;
import restaurante.team3.giacobello.payments.exceptions.PaymentsNotConfiguredException;
import restaurante.team3.giacobello.product.exceptions.ProductAlreadyExistsException;
import restaurante.team3.giacobello.product.exceptions.ProductNotFoundException;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @ParameterizedTest
    @CsvSource({
            "category-not-found, 404, Not Found, No existe la categoría",
            "category-already-exists, 409, Conflict, La categoría ya existe",
            "category-has-products, 409, Conflict, La categoría tiene productos",
            "product-not-found, 404, Not Found, No existe el producto",
            "product-already-exists, 409, Conflict, El producto ya existe",
            "payments-not-configured, 503, Service Unavailable, Los pagos con tarjeta no están disponibles",
            "payment-gateway, 502, Bad Gateway, No se pudo contactar con el proveedor de pagos"
    })
    void mapsEachDomainExceptionToItsStatusAndErrorBody(
            String exception,
            int expectedStatus,
            String expectedError,
            String expectedMessage) throws Exception {
        mockMvc.perform(get("/throw/{exception}", exception))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.error").value(expectedError))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @RestController
    static class ThrowingController {

        @GetMapping("/throw/{exception}")
        void throwException(@PathVariable String exception) {
            throw switch (exception) {
                case "category-not-found" -> new CategoryNotFoundException("No existe la categoría");
                case "category-already-exists" -> new CategoryAlreadyExistsException("La categoría ya existe");
                case "category-has-products" -> new CategoryHasProductsException("La categoría tiene productos");
                case "product-not-found" -> new ProductNotFoundException("No existe el producto");
                case "product-already-exists" -> new ProductAlreadyExistsException("El producto ya existe");
                case "payments-not-configured" -> new PaymentsNotConfiguredException();
                case "payment-gateway" -> new PaymentGatewayException(new RuntimeException("stripe detail"));
                default -> new IllegalArgumentException(exception);
            };
        }
    }
}
