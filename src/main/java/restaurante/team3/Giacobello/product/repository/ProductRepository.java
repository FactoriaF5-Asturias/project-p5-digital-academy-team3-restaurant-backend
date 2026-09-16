package restaurante.team3.Giacobello.product.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import restaurante.team3.Giacobello.product.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    boolean existsByCategoryId(Integer categoryId);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);

    List<ProductEntity> findAllByStatusTrue(Sort sort);
}
