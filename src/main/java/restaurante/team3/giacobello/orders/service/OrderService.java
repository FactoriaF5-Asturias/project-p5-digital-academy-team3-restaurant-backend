package restaurante.team3.giacobello.orders.service;

import java.util.List;

import restaurante.team3.giacobello.orders.dto.OrderCreateDTORequest;
import restaurante.team3.giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.giacobello.orders.dto.OrderStatusUpdateDTORequest;

public interface OrderService {
    List<OrderDTOResponse> findAll();

    OrderDTOResponse findById(Integer id);

    OrderDTOResponse create(OrderCreateDTORequest request);

    OrderDTOResponse updateStatus(Integer id, OrderStatusUpdateDTORequest request);
}
