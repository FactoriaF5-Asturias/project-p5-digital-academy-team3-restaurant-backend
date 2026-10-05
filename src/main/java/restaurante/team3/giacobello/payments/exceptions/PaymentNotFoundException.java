package restaurante.team3.giacobello.payments.exceptions;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException() {
        super("El pago no existe");
    }
}
