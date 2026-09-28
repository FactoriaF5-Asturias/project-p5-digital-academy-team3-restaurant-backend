package restaurante.team3.giacobello.products.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import restaurante.team3.giacobello.product.controller.ProductController;
import restaurante.team3.giacobello.product.dtos.ProductDTORequest;
import restaurante.team3.giacobello.product.dtos.ProductDTOResponse;
import restaurante.team3.giacobello.product.service.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock 
    private ProductService productService;

    @InjectMocks 
    private ProductController productController;


    @Test 
    @DisplayName("Comportamiento producto creado")
    void shouldCreateProduct() {
        ProductDTORequest request = new ProductDTORequest("Albaricoque", "Albaricoque fresco del monte", 1,
            new BigDecimal("4.50"), "/mega-albaricoque4K.png", true);

        ProductDTOResponse expectedResponse = new ProductDTOResponse(1, "Albaricoque",
            "Albaricoque fresco del monte", "Especialidades", new BigDecimal("4.50"), true,
            "/mega-albaricoque4K.png");

        when(productService.create(request)).thenReturn(expectedResponse);

        ResponseEntity<ProductDTOResponse> response = productController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(productService).create(request);
    }
}
