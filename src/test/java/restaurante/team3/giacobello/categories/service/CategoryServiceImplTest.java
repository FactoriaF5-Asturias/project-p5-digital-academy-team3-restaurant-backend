package restaurante.team3.giacobello.categories.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import restaurante.team3.giacobello.categories.dto.CategoryDTORequest;
import restaurante.team3.giacobello.categories.dto.CategoryDTOResponse;
import restaurante.team3.giacobello.categories.entity.CategoryEntity;
import restaurante.team3.giacobello.categories.exceptions.CategoryAlreadyExistsException;
import restaurante.team3.giacobello.categories.exceptions.CategoryHasProductsException;
import restaurante.team3.giacobello.categories.exceptions.CategoryNotFoundException;
import restaurante.team3.giacobello.categories.mappers.CategoryMapper;
import restaurante.team3.giacobello.categories.repository.CategoryRepository;
import restaurante.team3.giacobello.product.repository.ProductRepository;

class CategoryServiceImplTest {

    private final CategoryRepository repository = mock(CategoryRepository.class);
    private final CategoryMapper mapper = mock(CategoryMapper.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final CategoryServiceImpl service = new CategoryServiceImpl(repository, mapper, productRepository);

    @Test
    void findAllReturnsCategories() {
        CategoryEntity category = new CategoryEntity(1, "Pizzas");
        CategoryDTOResponse dto = new CategoryDTOResponse(1, "Pizzas");

        when(repository.findAll(Sort.by("name").ascending()))
                .thenReturn(List.of(category));
        when(mapper.toResponse(category)).thenReturn(dto);
        List<CategoryDTOResponse> result = service.findAll();

        assertEquals(List.of(dto), result);
    }

    @Test
    void deleteThrowsWhenCategoryHasProducts() {
        CategoryEntity category = new CategoryEntity(1, "Pizzas");
        when(repository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(1)).thenReturn(true);

        assertThrows(CategoryHasProductsException.class, () -> service.delete(1));
        verify(repository, never()).delete(category);
    }

    @Test
    void findByIdReturnsCategoryMappedToDto() {
        CategoryEntity category = new CategoryEntity(1, "Pizzas");
        CategoryDTOResponse dto = new CategoryDTOResponse(1, "Pizzas");
        when(repository.findById(1)).thenReturn(Optional.of(category));
        when(mapper.toResponse(category)).thenReturn(dto);

        CategoryDTOResponse result = service.findById(1);

        assertEquals(dto, result);
    }
}
