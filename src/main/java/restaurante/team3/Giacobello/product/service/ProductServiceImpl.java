package restaurante.team3.Giacobello.product.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import restaurante.team3.Giacobello.product.dtos.ProductDTOResponse;
import restaurante.team3.Giacobello.product.entity.ProductEntity;
import restaurante.team3.Giacobello.product.mappers.ProductMapper;
import restaurante.team3.Giacobello.product.repository.ProductRepository;

@Service 
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    public ProductServiceImpl(
        ProductRepository productRepository,
        ProductMapper productMapper) {

            this.productRepository = productRepository;
            this.productMapper = productMapper;
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
    @Transactional(readOnly = true)
    public ProductDTOResponse findById(Integer id) {
        ProductEntity product = productRepository.findById(id)
            .orElseThrow();
        return productMapper.toDto(product);
    }
}
