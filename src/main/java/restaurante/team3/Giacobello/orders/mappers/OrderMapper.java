package restaurante.team3.Giacobello.orders.mappers;

import org.springframework.stereotype.Component;

import restaurante.team3.Giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.Giacobello.orders.entity.OrderEntity;

@Component
public class OrderMapper {

    public OrderDTOResponse toResponse(OrderEntity order) {
        return new OrderDTOResponse(
                order.getId(),
                order.getTabletId(),
                order.getStatusName(),
                order.getTotalAmount(),
                order.getCreatedAt());
    }
}
