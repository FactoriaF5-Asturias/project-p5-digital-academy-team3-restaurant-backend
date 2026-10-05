package restaurante.team3.giacobello.orders.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import restaurante.team3.giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.giacobello.orders.dto.OrderItemDTOResponse;
import restaurante.team3.giacobello.orders.entity.OrderEntity;
import restaurante.team3.giacobello.orders.entity.OrderItemEntity;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTOResponse toResponse(OrderEntity order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemDTOResponse toItemResponse(OrderItemEntity item);
}
