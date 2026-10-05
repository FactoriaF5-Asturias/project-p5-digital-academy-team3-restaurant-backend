package restaurante.team3.giacobello.payments.exceptions;

public class PaymentGatewayException extends RuntimeException {

    public PaymentGatewayException(Throwable cause) {
        super("No se pudo contactar con el proveedor de pagos", cause);
    }
}
