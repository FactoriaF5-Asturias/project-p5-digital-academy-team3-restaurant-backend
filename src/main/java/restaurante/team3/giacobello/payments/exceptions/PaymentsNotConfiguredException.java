package restaurante.team3.giacobello.payments.exceptions;

public class PaymentsNotConfiguredException extends RuntimeException {

    public PaymentsNotConfiguredException() {
        super("Los pagos con tarjeta no están configurados: falta STRIPE_SECRET_KEY");
    }
}
