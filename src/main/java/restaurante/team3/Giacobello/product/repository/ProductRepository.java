package restaurante.team3.Giacobello.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurante.team3.Giacobello.product.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    boolean existsByCategoryId(Integer categoryId);
}
