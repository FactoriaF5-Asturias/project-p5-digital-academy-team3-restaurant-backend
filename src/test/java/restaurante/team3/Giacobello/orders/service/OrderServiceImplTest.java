package restaurante.team3.Giacobello.orders.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import restaurante.team3.Giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.Giacobello.orders.entity.OrderEntity;
import restaurante.team3.Giacobello.orders.mappers.OrderMapper;
import restaurante.team3.Giacobello.orders.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
                "DINE_IN",
                "CARD",
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

    @Test
    void shouldReturnOrderById() {
        OrderEntity order = new OrderEntity();
        OrderDTOResponse response = new OrderDTOResponse(
                1,
                2,
                "DINE IN",
                "CASH",
                "PENDING",
                null,
                null);
        when(orderRepository.findById(1))
                .thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order))
                .thenReturn(response);
        OrderDTOResponse result = orderService.findById(1);
        assertEquals(response, result);
        verify(orderRepository).findById(1);
        verify(orderMapper).toResponse(order);
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() {
        when(orderRepository.findById(99))
                .thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.findById(99));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(orderMapper);
    }
}
