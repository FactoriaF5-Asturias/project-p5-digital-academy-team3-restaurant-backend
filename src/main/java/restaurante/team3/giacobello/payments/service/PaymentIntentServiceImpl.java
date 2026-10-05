package restaurante.team3.giacobello.payments.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.orders.dto.OrderItemCreateDTORequest;
import restaurante.team3.giacobello.orders.service.OrderPricing;
import restaurante.team3.giacobello.orders.service.OrderTotalCalculator;
import restaurante.team3.giacobello.payments.dto.PaymentIntentCreateDTORequest;
import restaurante.team3.giacobello.payments.dto.PaymentIntentDTOResponse;
import restaurante.team3.giacobello.payments.gateway.PaymentIntentCreated;
import restaurante.team3.giacobello.payments.gateway.StripePaymentGateway;
import restaurante.team3.giacobello.product.entity.ProductEntity;
import restaurante.team3.giacobello.product.repository.ProductRepository;

@Service
public class PaymentIntentServiceImpl implements PaymentIntentService {

    static final String CURRENCY = "eur";
    static final long MIN_AMOUNT_IN_CENTS = 50;

    private final ProductRepository productRepository;
    private final OrderTotalCalculator orderTotalCalculator;
    private final StripePaymentGateway paymentGateway;

    public PaymentIntentServiceImpl(
            ProductRepository productRepository,
            OrderTotalCalculator orderTotalCalculator,
            StripePaymentGateway paymentGateway) {
        this.productRepository = productRepository;
        this.orderTotalCalculator = orderTotalCalculator;
        this.paymentGateway = paymentGateway;
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentIntentDTOResponse createIntent(PaymentIntentCreateDTORequest request) {
        orderTotalCalculator.validateItems(request.items());

        List<Integer> productIds = request.items()
                .stream()
                .map(OrderItemCreateDTORequest::productId)
                .toList();

        Map<Integer, ProductEntity> products = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(
                        ProductEntity::getId,
                        Function.identity()));

        OrderPricing pricing = orderTotalCalculator.calculate(request.items(), products);
        long amountInCents = toCents(pricing.total());

        if (amountInCents < MIN_AMOUNT_IN_CENTS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El importe mínimo para pagar con tarjeta es 0,50 €");
        }

        PaymentIntentCreated intent = paymentGateway.createPaymentIntent(amountInCents, CURRENCY);

        return new PaymentIntentDTOResponse(intent.id(), intent.clientSecret(), pricing.total());
    }

    private long toCents(BigDecimal total) {
        return total.movePointRight(2).longValueExact();
    }
}
