package restaurante.team3.Giacobello.product.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import restaurante.team3.Giacobello.categories.repository.CategoryRepository;
import restaurante.team3.Giacobello.categories.entity.CategoryEntity;
import restaurante.team3.Giacobello.categories.exceptions.CategoryNotFoundException;
import restaurante.team3.Giacobello.product.dtos.ProductDTORequest;
import restaurante.team3.Giacobello.product.dtos.ProductDTOResponse;
import restaurante.team3.Giacobello.product.entity.ProductEntity;
import restaurante.team3.Giacobello.product.exceptions.ProductAlreadyExistsException;
import restaurante.team3.Giacobello.product.mappers.ProductMapper;
import restaurante.team3.Giacobello.product.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ProductMapper productMapper,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTOResponse> findAll() {
        return productRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductDTOResponse create(ProductDTORequest request) {

        if (productRepository.existsByName(request.name())) {
            throw new ProductAlreadyExistsException("Product already exists with name: " + request.name());
        }

        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category not found with id : " + request.categoryId()));

        ProductEntity entity = productMapper.toEntity(request);
        entity.setCategory(category);

        ProductEntity saved = productRepository.save(entity);

        return productMapper.toDto(saved);
    }

}
