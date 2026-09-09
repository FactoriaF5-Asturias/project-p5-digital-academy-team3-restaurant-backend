package restaurante.team3.Giacobello.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurante.team3.Giacobello.dto.OrderDTOResponse;
import restaurante.team3.Giacobello.mappers.OrderMapper;
import restaurante.team3.Giacobello.repository.OrderRepository;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTOResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }
}
