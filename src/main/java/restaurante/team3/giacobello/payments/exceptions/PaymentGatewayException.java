package restaurante.team3.giacobello.payments.exceptions;

import com.stripe.exception.StripeException;

public class PaymentGatewayException extends RuntimeException {

    private final String causeType;
    private final String stripeCode;

    public PaymentGatewayException(Throwable cause) {
        super("No se pudo contactar con el proveedor de pagos", cause);
        this.causeType = cause.getClass().getSimpleName();
        this.stripeCode = cause instanceof StripeException stripeException ? stripeException.getCode() : null;
    }

    public String getCauseType() {
        return causeType;
    }

    public String getStripeCode() {
        return stripeCode;
    }
}
