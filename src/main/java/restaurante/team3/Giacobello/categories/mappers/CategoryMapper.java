package restaurante.team3.Giacobello.categories.mappers;

import org.mapstruct.Mapper;
import restaurante.team3.Giacobello.categories.dto.CategoryDTOResponse;
import restaurante.team3.Giacobello.categories.entity.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTOResponse toResponse(CategoryEntity category);
}
