package restaurante.team3.Giacobello.service;

import restaurante.team3.Giacobello.dto.OrderDTOResponse;

import java.util.List;

public interface OrderService {
    List<OrderDTOResponse> findAll();
}
