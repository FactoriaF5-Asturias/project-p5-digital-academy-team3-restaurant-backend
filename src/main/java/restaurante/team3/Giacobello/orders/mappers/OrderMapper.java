package restaurante.team3.Giacobello.orders.mappers;

import org.mapstruct.Mapper;

import restaurante.team3.Giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.Giacobello.orders.entity.OrderEntity;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTOResponse toResponse(OrderEntity order);
}
