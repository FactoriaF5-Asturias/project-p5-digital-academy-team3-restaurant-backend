package restaurante.team3.Giacobello.product.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import restaurante.team3.Giacobello.product.dtos.ProductDTORequest;
import restaurante.team3.Giacobello.product.dtos.ProductDTOResponse;
import restaurante.team3.Giacobello.product.entity.ProductEntity;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    ProductEntity toEntity(ProductDTORequest request);

    @Mapping(source = "category.name", target = "category")
    ProductDTOResponse toDto(ProductEntity entity);
    
    @Mapping(source = "category.name", target = "category")
    List<ProductDTOResponse> toDtoList(List<ProductEntity> entities);

    
}
