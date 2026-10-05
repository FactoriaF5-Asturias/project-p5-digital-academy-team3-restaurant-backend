package restaurante.team3.giacobello.exceptions;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import restaurante.team3.giacobello.categories.exceptions.CategoryAlreadyExistsException;
import restaurante.team3.giacobello.categories.exceptions.CategoryHasProductsException;
import restaurante.team3.giacobello.categories.exceptions.CategoryNotFoundException;
import restaurante.team3.giacobello.payments.exceptions.PaymentGatewayException;
import restaurante.team3.giacobello.payments.exceptions.PaymentsNotConfiguredException;
import restaurante.team3.giacobello.product.exceptions.ProductAlreadyExistsException;
import restaurante.team3.giacobello.product.exceptions.ProductNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCategoryNotFound(CategoryNotFoundException ex) {

        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleCategoryAlreadyExists(CategoryAlreadyExistsException ex) {

        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(CategoryHasProductsException.class)
    public ResponseEntity<ApiErrorResponse> handleCategoryHasProducts(CategoryHasProductsException ex) {

        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(ProductNotFoundException ex) {

        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleProductAlreadyExists(ProductAlreadyExistsException ex) {

        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(PaymentsNotConfiguredException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentsNotConfigured(PaymentsNotConfiguredException ex) {

        log.warn("Pago con tarjeta solicitado pero Stripe no está configurado");

        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentGateway(PaymentGatewayException ex) {

        log.error("Fallo del proveedor de pagos: tipo={} codigo={}", ex.getCauseType(), ex.getStripeCode());

        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.BAD_GATEWAY.value(),
                HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

}
