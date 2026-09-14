package restaurante.team3.Giacobello.product;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import restaurante.team3.Giacobello.product.dtos.ProductDTOResponse;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.name", target = "category")

    ProductDTOResponse toDto(ProductEntity entity);

    List<ProductDTOResponse> toDtoList(List<ProductEntity> entities);
}
