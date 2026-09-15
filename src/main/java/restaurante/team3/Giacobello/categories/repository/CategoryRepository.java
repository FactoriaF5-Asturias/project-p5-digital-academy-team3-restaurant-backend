package restaurante.team3.Giacobello.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurante.team3.Giacobello.categories.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);

}
