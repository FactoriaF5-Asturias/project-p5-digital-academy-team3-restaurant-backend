package restaurante.team3.Giacobello.orders.service;

import java.util.List;

import restaurante.team3.Giacobello.orders.dto.OrderDTOResponse;

public interface OrderService {
    List<OrderDTOResponse> findAll();
}
