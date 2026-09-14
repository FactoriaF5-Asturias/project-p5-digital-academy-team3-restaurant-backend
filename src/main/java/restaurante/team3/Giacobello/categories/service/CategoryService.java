package restaurante.team3.Giacobello.categories.service;

import java.util.List;
import restaurante.team3.Giacobello.categories.dto.CategoryDTOResponse;

public interface CategoryService {
    List<CategoryDTOResponse> findAll();
}
