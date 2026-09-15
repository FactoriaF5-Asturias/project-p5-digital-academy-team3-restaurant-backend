package restaurante.team3.Giacobello.orders.service;

import java.util.List;

import restaurante.team3.Giacobello.orders.dto.OrderCreateDTORequest;
import restaurante.team3.Giacobello.orders.dto.OrderDTOResponse;

public interface OrderService {
    List<OrderDTOResponse> findAll();

    OrderDTOResponse findById(Integer id);

    OrderDTOResponse create(OrderCreateDTORequest request);
}
