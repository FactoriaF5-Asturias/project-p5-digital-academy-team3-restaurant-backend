package restaurante.team3.giacobello.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import restaurante.team3.giacobello.categories.entity.CategoryEntity;
import restaurante.team3.giacobello.categories.exceptions.CategoryNotFoundException;
import restaurante.team3.giacobello.categories.repository.CategoryRepository;
import restaurante.team3.giacobello.product.dtos.ProductDTORequest;
import restaurante.team3.giacobello.product.dtos.ProductDTOResponse;
import restaurante.team3.giacobello.product.entity.ProductEntity;
import restaurante.team3.giacobello.product.exceptions.ProductAlreadyExistsException;
import restaurante.team3.giacobello.product.exceptions.ProductNotFoundException;
import restaurante.team3.giacobello.product.mappers.ProductMapper;
import restaurante.team3.giacobello.product.repository.ProductRepository;

class ProductServiceImplTest {

        private final ProductRepository productRepository = mock(ProductRepository.class);
        private final ProductMapper productMapper = mock(ProductMapper.class);
        private final CategoryRepository categoryRepository = mock(CategoryRepository.class);

        private final ProductServiceImpl service = new ProductServiceImpl(productRepository, productMapper,
                        categoryRepository);

        @Test
        void findAllReturnsActiveProductsMappedToDto() {
                ProductEntity entity = new ProductEntity();
                ProductDTOResponse dto = new ProductDTOResponse(
                                1, "Pizza", "Rica", "Especialidades", new BigDecimal("10.00"), true, "/img.jpg");

                when(productRepository.findAllByStatusTrue(Sort.by("name").ascending()))
                                .thenReturn(List.of(entity));
                when(productMapper.toDto(entity)).thenReturn(dto);

                List<ProductDTOResponse> result = service.findAll();

                assertEquals(List.of(dto), result);
        }

        @Test
        void findAllReturnsEmptyListWhenNoProducts() {
                when(productRepository.findAllByStatusTrue(Sort.by("name").ascending()))
                                .thenReturn(List.of());

                List<ProductDTOResponse> result = service.findAll();

                assertEquals(List.of(), result);

        }

        @Test
        void findAllOnlyRequestsActiveProducts() {
                when(productRepository.findAllByStatusTrue(Sort.by("name").ascending()))
                                .thenReturn(List.of());

                service.findAll();

                verify(productRepository).findAllByStatusTrue(Sort.by("name").ascending());
                verify(productRepository, never()).findAll();
        }

        @Test
        void findByIdReturnsProductMappedToDto() {
                ProductEntity entity = new ProductEntity();
                ProductDTOResponse dto = new ProductDTOResponse(
                                1, "Pizza", "Rica", "Especialidades", new BigDecimal("10.00"), true, "/img.jpg");

                when(productRepository.findById(1)).thenReturn(Optional.of(entity));
                when(productMapper.toDto(entity)).thenReturn(dto);

                ProductDTOResponse result = service.findById(1);

                assertEquals(dto, result);
        }

        @Test
        void deleteThrowsWhenProductDoesNotExist() {
                when(productRepository.findById(99)).thenReturn(Optional.empty());

                assertThrows(ProductNotFoundException.class, () -> service.delete(99));
        }

        private final ProductDTORequest request = new ProductDTORequest(
                        "Pizza", "Rica", 2, new BigDecimal("10.00"), "/img.jpg", true);

        private final ProductDTOResponse dto = new ProductDTOResponse(
                        1, "Pizza", "Rica", "Especialidades", new BigDecimal("10.00"), true, "/img.jpg");

        @Test
        void findByIdThrowsWhenProductDoesNotExist() {
                when(productRepository.findById(99)).thenReturn(Optional.empty());

                assertThrows(NoSuchElementException.class, () -> service.findById(99));
        }

        @Test
        void deleteMarksProductAsInactive() {
                ProductEntity product = new ProductEntity();
                product.setStatus(true);
                when(productRepository.findById(1)).thenReturn(Optional.of(product));

                service.delete(1);

                assertFalse(product.getStatus());
                verify(productRepository).save(product);
        }

        @Test
        void createSavesProductWithItsCategory() {
                CategoryEntity category = new CategoryEntity();
                ProductEntity entity = new ProductEntity();
                when(productRepository.existsByName("Pizza")).thenReturn(false);
                when(categoryRepository.findById(2)).thenReturn(Optional.of(category));
                when(productMapper.toEntity(request)).thenReturn(entity);
                when(productRepository.save(entity)).thenReturn(entity);
                when(productMapper.toDto(entity)).thenReturn(dto);

                ProductDTOResponse result = service.create(request);

                assertEquals(dto, result);
                assertSame(category, entity.getCategory());
        }
}
