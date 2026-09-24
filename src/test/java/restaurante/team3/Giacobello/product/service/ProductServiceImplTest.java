package restaurante.team3.Giacobello.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import restaurante.team3.Giacobello.categories.repository.CategoryRepository;
import restaurante.team3.Giacobello.product.dtos.ProductDTOResponse;
import restaurante.team3.Giacobello.product.entity.ProductEntity;
import restaurante.team3.Giacobello.product.mappers.ProductMapper;
import restaurante.team3.Giacobello.product.repository.ProductRepository;

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
}
