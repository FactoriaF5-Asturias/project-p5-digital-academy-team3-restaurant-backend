package team3.dev.restaurante.global.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class ApiResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void omitsNullFieldsWhenSerialized() throws Exception {
        String json = objectMapper.writeValueAsString(ApiResponse.ok("hello"));

        assertThat(json).isEqualTo("""
                {"success":true,"data":"hello"}""".strip());
    }

    @Test
    void keepsErrorMessageWhenFailure() throws Exception {
        String json = objectMapper.writeValueAsString(ApiResponse.failure("Product not found"));

        assertThat(json).isEqualTo("""
                {"success":false,"error":"Product not found"}""".strip());
    }
}
