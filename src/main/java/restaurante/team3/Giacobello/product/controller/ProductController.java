package restaurante.team3.Giacobello.product.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import restaurante.team3.Giacobello.product.service.ProductService;

import restaurante.team3.Giacobello.product.dtos.ProductDTOResponse;

@RestController
@RequestMapping ("${api-endpoint}/products") 
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping 
    public ResponseEntity<List<ProductDTOResponse>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }
}
