package restaurante.team3.Giacobello.product.service;

import java.util.List;

import restaurante.team3.Giacobello.product.dtos.ProductDTORequest;
import restaurante.team3.Giacobello.product.dtos.ProductDTOResponse;

public interface ProductService {
    List<ProductDTOResponse> findAll();

    ProductDTOResponse create(ProductDTORequest request);

}
