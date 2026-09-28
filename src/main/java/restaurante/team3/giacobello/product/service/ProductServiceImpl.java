package restaurante.team3.giacobello.product.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return productRepository.findAllByStatusTrue(Sort.by("name").ascending())
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

    @Override
    @Transactional
    public ProductDTOResponse update(Integer id, ProductDTORequest request) {

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id : " + id));

        if (productRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ProductAlreadyExistsException("Product already exists with name: " + request.name());
        }

        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category not found with id : " + request.categoryId()));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(category);
        product.setPrice(request.price());
        product.setStatus(request.status());
        product.setImageUrl(request.imageUrl());

        ProductEntity saved = productRepository.save(product);

        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Integer id) {

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id : " + id));

        product.setStatus(false);

        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public ProductDTOResponse findById(Integer id) {
        ProductEntity product = productRepository.findById(id)
            .orElseThrow();
        return productMapper.toDto(product);
    }
}
