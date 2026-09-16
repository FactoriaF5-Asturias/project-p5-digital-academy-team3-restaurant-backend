package restaurante.team3.Giacobello.categories.service;

import java.util.List;

import restaurante.team3.Giacobello.categories.dto.CategoryDTORequest;
import restaurante.team3.Giacobello.categories.dto.CategoryDTOResponse;

public interface CategoryService {
    List<CategoryDTOResponse> findAll();

    CategoryDTOResponse findById(Integer id);

    CategoryDTOResponse create(CategoryDTORequest request);

    CategoryDTOResponse update(Integer id, CategoryDTORequest request);

    void delete(Integer id);
}
