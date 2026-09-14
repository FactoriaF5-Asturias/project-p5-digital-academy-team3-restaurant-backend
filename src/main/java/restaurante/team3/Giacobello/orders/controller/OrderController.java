package restaurante.team3.Giacobello.orders.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import restaurante.team3.Giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.Giacobello.orders.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTOResponse>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }
}
