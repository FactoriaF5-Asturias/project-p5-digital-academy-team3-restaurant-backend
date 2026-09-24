package restaurante.team3.giacobello.categories.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import restaurante.team3.giacobello.categories.dto.CategoryDTOResponse;
import restaurante.team3.giacobello.categories.entity.CategoryEntity;
import restaurante.team3.giacobello.categories.mappers.CategoryMapper;
import restaurante.team3.giacobello.categories.repository.CategoryRepository;
import restaurante.team3.giacobello.categories.service.CategoryServiceImpl;
import restaurante.team3.giacobello.product.repository.ProductRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Test
    void findAllReturnsCategories() {
        CategoryRepository repository = mock(CategoryRepository.class);
        CategoryMapper mapper = mock(CategoryMapper.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        CategoryServiceImpl service = new CategoryServiceImpl(repository, mapper, productRepository);

        CategoryEntity category = new CategoryEntity(1, "Pizzas");
        CategoryDTOResponse dto = new CategoryDTOResponse(1, "Pizzas");

        when(repository.findAll(Sort.by("name").ascending()))
                .thenReturn(List.of(category));
        when(mapper.toResponse(category)).thenReturn(dto);
        List<CategoryDTOResponse> result = service.findAll();

        assertEquals(List.of(dto), result);
    }
}