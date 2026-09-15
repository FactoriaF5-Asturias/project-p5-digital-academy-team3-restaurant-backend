package restaurante.team3.Giacobello.orders.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.Giacobello.orders.dto.OrderCreateDTORequest;
import restaurante.team3.Giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.Giacobello.orders.dto.OrderItemCreateDTORequest;
import restaurante.team3.Giacobello.orders.entity.OrderEntity;
import restaurante.team3.Giacobello.orders.entity.OrderItemEntity;
import restaurante.team3.Giacobello.orders.mappers.OrderMapper;
import restaurante.team3.Giacobello.orders.repository.OrderItemRepository;
import restaurante.team3.Giacobello.orders.repository.OrderRepository;
import restaurante.team3.Giacobello.product.entity.ProductEntity;
import restaurante.team3.Giacobello.product.repository.ProductRepository;
//import restaurante.team3.Giacobello.tablets.repository.TabletRepository;

@Service
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal MAX_TOTAL = new BigDecimal("99999999.99");

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    //private final TabletRepository tabletRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            ProductRepository productRepository,
            //TabletRepository tabletRepository,
            OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.productRepository = productRepository;
        //this.tabletRepository = tabletRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTOResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTOResponse findById(Integer id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe el pedido con ID " + id));

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderDTOResponse create(OrderCreateDTORequest request) {
        validateRequest(request);

        List<Integer> productIds = request.items()
                .stream()
                .map(OrderItemCreateDTORequest::productId)
                .toList();

        Map<Integer, ProductEntity> products = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(
                        ProductEntity::getId,
                        Function.identity()));

        OrderEntity order = new OrderEntity();
        order.setTabletId(request.tabletId());
        order.setOrderTypeName(request.orderTypeName());
        order.setPaymentMethodName(request.paymentMethodName());
        order.setStatusName("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItemEntity> lines = new ArrayList<>();
        BigDecimal total = new BigDecimal("0.00");

        for (OrderItemCreateDTORequest item : request.items()) {
            ProductEntity product = products.get(item.productId());

            if (product == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No existe el producto " + item.productId());
            }

            if (!Boolean.TRUE.equals(product.getStatus())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El producto " + product.getId()
                                + " no está disponible");
            }

            BigDecimal unitPrice = product.getPrice();

            if (unitPrice == null || unitPrice.signum() < 0) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "No se pudo calcular el importe del pedido");
            }

            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(item.quantity()));

            total = total.add(subtotal);

            if (total.compareTo(MAX_TOTAL) > 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El importe supera el máximo permitido");
            }

            lines.add(new OrderItemEntity(
                    order,
                    product,
                    item.quantity(),
                    unitPrice,
                    subtotal));
        }

        order.setTotalAmount(total);

        OrderEntity savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(lines);

        return orderMapper.toResponse(savedOrder);
    }

    private void validateRequest(OrderCreateDTORequest request) {
        if (!"DINE IN".equals(request.orderTypeName())
                || !"CASH".equals(request.paymentMethodName())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Esta versión admite DINE IN con CASH");
        }

        if (request.tabletId() == null || request.tabletId() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La mesa es obligatoria");
        }

        if (!tabletRepository.existsById(request.tabletId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La mesa no existe");
        }

        if (request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El pedido debe contener productos");
        }

        var seenProducts = new HashSet<Integer>();

        for (OrderItemCreateDTORequest item : request.items()) {
            if (item == null
                    || item.productId() == null
                    || item.productId() <= 0
                    || item.quantity() == null
                    || item.quantity() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Producto o cantidad inválidos");
            }

            if (!seenProducts.add(item.productId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No se puede repetir un producto; aumenta su cantidad");
            }
        }
    }
}
