package restaurante.team3.giacobello.orders.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.invoices.entity.InvoiceEntity;
import restaurante.team3.giacobello.invoices.repository.InvoiceRepository;
import restaurante.team3.giacobello.orders.dto.OrderCreateDTORequest;
import restaurante.team3.giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.giacobello.orders.dto.OrderItemCreateDTORequest;
import restaurante.team3.giacobello.orders.dto.OrderStatusUpdateDTORequest;
import restaurante.team3.giacobello.orders.entity.OrderEntity;
import restaurante.team3.giacobello.orders.entity.OrderItemEntity;
import restaurante.team3.giacobello.orders.mappers.OrderMapper;
import restaurante.team3.giacobello.orders.repository.OrderItemRepository;
import restaurante.team3.giacobello.orders.repository.OrderRepository;
import restaurante.team3.giacobello.payments.gateway.PaymentIntentDetails;
import restaurante.team3.giacobello.payments.gateway.StripePaymentGateway;
import restaurante.team3.giacobello.product.entity.ProductEntity;
import restaurante.team3.giacobello.product.repository.ProductRepository;
import restaurante.team3.giacobello.tablets.repository.TabletRepository;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Set<String> ALLOWED_STATUS_NAMES = Set.of(
            "PENDING",
            "CANCELLED",
            "ACCEPTED",
            "COMPLETED",
            "DELAYED",
            "IN PROGRESS");
    private static final Set<String> ALLOWED_ORDER_TYPE_NAMES = Set.of("DINE IN", "TAKEAWAY");
    private static final String PAYMENT_SUCCEEDED = "succeeded";
    private static final Set<String> ALLOWED_PAYMENT_METHOD_NAMES = Set.of("CASH", "CARD");
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final TabletRepository tabletRepository;
    private final OrderItemRepository orderItemRepository;
    private final InvoiceRepository invoiceRepository;
    private final OrderTotalCalculator orderTotalCalculator;
    private final StripePaymentGateway paymentGateway;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            ProductRepository productRepository,
            TabletRepository tabletRepository,
            OrderItemRepository orderItemRepository,
            InvoiceRepository invoiceRepository,
            OrderTotalCalculator orderTotalCalculator,
            StripePaymentGateway paymentGateway) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.productRepository = productRepository;
        this.tabletRepository = tabletRepository;
        this.orderItemRepository = orderItemRepository;
        this.invoiceRepository = invoiceRepository;
        this.orderTotalCalculator = orderTotalCalculator;
        this.paymentGateway = paymentGateway;
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

        OrderPricing pricing = orderTotalCalculator.calculate(request.items(), products);

        List<OrderItemEntity> lines = pricing.lines()
                .stream()
                .map(line -> new OrderItemEntity(
                        order,
                        line.product(),
                        line.quantity(),
                        line.unitPrice(),
                        line.subtotal()))
                .toList();

        order.setTotalAmount(pricing.total());

        String paymentIntentId = request.paymentIntentId();
        boolean paidOnline = paymentIntentId != null;

        if (paidOnline) {
            verifyPaymentIntent(paymentIntentId, pricing.totalInCents());
            order.setStripePaymentIntentId(paymentIntentId);
            order.setPaidAt(order.getCreatedAt());
        }

        OrderEntity savedOrder = saveOrder(order, paidOnline);
        orderItemRepository.saveAll(lines);
        savedOrder.getItems().addAll(lines);

        if (paidOnline) {
            createInvoice(savedOrder, savedOrder.getPaidAt());
        }

        return orderMapper.toResponse(savedOrder);
    }

    private OrderEntity saveOrder(OrderEntity order, boolean paidOnline) {
        try {
            return orderRepository.save(order);
        } catch (DataIntegrityViolationException e) {
            if (paidOnline) {
                throw paymentAlreadyUsed();
            }
            throw e;
        }
    }

    private void verifyPaymentIntent(String paymentIntentId, long expectedAmountInCents) {
        if (orderRepository.existsByStripePaymentIntentId(paymentIntentId)) {
            throw paymentAlreadyUsed();
        }

        PaymentIntentDetails details = paymentGateway.retrievePaymentIntent(paymentIntentId);

        if (!PAYMENT_SUCCEEDED.equals(details.status())) {
            throw new ResponseStatusException(
                    HttpStatus.PAYMENT_REQUIRED,
                    "El pago con tarjeta no se ha completado");
        }

        if (!StripePaymentGateway.CURRENCY.equalsIgnoreCase(details.currency())
                || details.amountInCents() != expectedAmountInCents) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El importe pagado no coincide con el total del pedido");
        }
    }

    private ResponseStatusException paymentAlreadyUsed() {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El pago con tarjeta ya se ha utilizado en otro pedido");
    }

    private void createInvoice(OrderEntity order, LocalDateTime paidAt) {
        invoiceRepository.save(new InvoiceEntity(
                order.getId(),
                "INV-" + order.getId(),
                order.getTotalAmount(),
                paidAt));
    }

    private void validateRequest(OrderCreateDTORequest request) {
        if (!ALLOWED_ORDER_TYPE_NAMES.contains(request.orderTypeName())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de pedido no admitido: usa DINE IN o TAKEAWAY");
        }

        if (!ALLOWED_PAYMENT_METHOD_NAMES.contains(request.paymentMethodName())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Método de pago no admitido: usa CASH o CARD");
        }

        if (request.paymentIntentId() != null) {
            if (request.paymentIntentId().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El identificador de pago no es válido");
            }

            if (!"CARD".equals(request.paymentMethodName())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Solo se puede enviar un pago previo con el método CARD");
            }
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

        orderTotalCalculator.validateItems(request.items());
    }

    @Override
    @Transactional
    public OrderDTOResponse updateStatus(
            Integer id,
            OrderStatusUpdateDTORequest request) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe el pedido con ID " + id));

        String statusName = request.statusName()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!ALLOWED_STATUS_NAMES.contains(statusName)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado no válido: " + request.statusName());
        }

        if ("CANCELLED".equals(statusName) && !"PENDING".equals(order.getStatusName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden rechazar pedidos pendientes");
        }

        order.setStatusName(statusName);

        OrderEntity savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderDTOResponse pay(Integer id) {
        OrderEntity order = orderRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe el pedido con ID " + id));

        if (order.getPaidAt() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El pedido " + id + " ya está pagado");
        }

        if ("CANCELLED".equals(order.getStatusName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede cobrar el pedido cancelado " + id);
        }

        LocalDateTime paidAt = LocalDateTime.now();
        order.setPaidAt(paidAt);
        OrderEntity savedOrder = orderRepository.save(order);

        createInvoice(savedOrder, paidAt);

        return orderMapper.toResponse(savedOrder);
    }
}
