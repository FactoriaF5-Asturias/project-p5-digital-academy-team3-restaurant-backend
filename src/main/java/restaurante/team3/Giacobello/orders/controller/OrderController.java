package restaurante.team3.Giacobello.orders.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import restaurante.team3.Giacobello.orders.dto.OrderCreateDTORequest;
import restaurante.team3.Giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.Giacobello.orders.dto.OrderStatusUpdateDTORequest;
import restaurante.team3.Giacobello.orders.service.OrderService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api-endpoint}/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTOResponse>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTOResponse> findById(
            @PathVariable("id") Integer id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDTOResponse> create(
            @Valid @RequestBody OrderCreateDTORequest request) {
        OrderDTOResponse response = orderService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTOResponse> updateStatus(
            @PathVariable("id") Integer id,
            @Valid @RequestBody OrderStatusUpdateDTORequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request));
    }
}
