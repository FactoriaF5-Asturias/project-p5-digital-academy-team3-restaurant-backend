package restaurante.team3.giacobello.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurante.team3.giacobello.categories.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);

}
