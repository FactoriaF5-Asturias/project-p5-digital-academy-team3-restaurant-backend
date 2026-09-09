package restaurante.team3.Giacobello.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import restaurante.team3.Giacobello.dto.OrderDTOResponse;
import restaurante.team3.Giacobello.entity.OrderEntity;
import restaurante.team3.Giacobello.mappers.OrderMapper;
import restaurante.team3.Giacobello.repository.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldReturnAllOrders() {
        OrderEntity order = new OrderEntity();
        OrderDTOResponse response = new OrderDTOResponse(
                1,
                2,
                "IN_PROGRESS",
                null,
                null);

        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toResponse(order)).thenReturn(response);

        List<OrderDTOResponse> result = orderService.findAll();

        assertEquals(List.of(response), result);
        verify(orderRepository).findAll();
        verify(orderMapper).toResponse(order);
    }
}
