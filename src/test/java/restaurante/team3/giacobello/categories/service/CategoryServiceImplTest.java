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

    @Test
    void findByIdThrowsWhenCategoryDoesNotExist() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> service.findById(99));
    }

    @Test
    void createSavesCategory() {
        CategoryDTORequest request = new CategoryDTORequest("Pizzas");
        CategoryEntity entity = new CategoryEntity(null, "Pizzas");
        CategoryEntity saved = new CategoryEntity(1, "Pizzas");
        CategoryDTOResponse dto = new CategoryDTOResponse(1, "Pizzas");
        when(repository.existsByName("Pizzas")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(dto);

        CategoryDTOResponse result = service.create(request);

        assertEquals(dto, result);
    }

    @Test
    void createThrowsWhenNameAlreadyExists() {
        CategoryDTORequest request = new CategoryDTORequest("Pizzas");
        when(repository.existsByName("Pizzas")).thenReturn(true);

        assertThrows(CategoryAlreadyExistsException.class, () -> service.create(request));
        verify(repository, never()).save(any());
    }

    @Test
    void updateSavesNewName() {
        CategoryDTORequest request = new CategoryDTORequest("Postres");
        CategoryEntity category = new CategoryEntity(1, "Pizzas");
        CategoryDTOResponse dto = new CategoryDTOResponse(1, "Postres");
        when(repository.findById(1)).thenReturn(Optional.of(category));
        when(repository.existsByNameAndIdNot("Postres", 1)).thenReturn(false);
        when(repository.save(category)).thenReturn(category);
        when(mapper.toResponse(category)).thenReturn(dto);

        CategoryDTOResponse result = service.update(1, request);

        assertEquals(dto, result);
        assertEquals("Postres", category.getName());
    }

    @Test
    void updateThrowsWhenCategoryDoesNotExist() {
        CategoryDTORequest request = new CategoryDTORequest("Postres");
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> service.update(99, request));
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenNameBelongsToAnotherCategory() {
        CategoryDTORequest request = new CategoryDTORequest("Postres");
        when(repository.findById(1)).thenReturn(Optional.of(new CategoryEntity(1, "Pizzas")));
        when(repository.existsByNameAndIdNot("Postres", 1)).thenReturn(true);

        assertThrows(CategoryAlreadyExistsException.class, () -> service.update(1, request));
        verify(repository, never()).save(any());
    }

    @Test
    void deleteRemovesCategoryWithoutProducts() {
        CategoryEntity category = new CategoryEntity(1, "Pizzas");
        when(repository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(1)).thenReturn(false);

        service.delete(1);

        verify(repository).delete(category);
    }

    @Test
    void deleteThrowsWhenCategoryDoesNotExist() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> service.delete(99));
        verify(repository, never()).delete(any());
    }
}
