package restaurante.team3.Giacobello.categories.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import restaurante.team3.Giacobello.categories.dto.CategoryDTORequest;
import restaurante.team3.Giacobello.categories.dto.CategoryDTOResponse;
import restaurante.team3.Giacobello.categories.service.CategoryService;

@RestController
@RequestMapping("${api-endpoint}/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTOResponse>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTOResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDTOResponse> create(@Valid @RequestBody CategoryDTORequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }
}
