package restaurante.team3.giacobello.payments.gateway;

import restaurante.team3.giacobello.payments.exceptions.PaymentsNotConfiguredException;

public class UnconfiguredPaymentGateway implements StripePaymentGateway {

    @Override
    public PaymentIntentCreated createPaymentIntent(long amountInCents, String currency) {
        throw new PaymentsNotConfiguredException();
    }

    @Override
    public PaymentIntentDetails retrievePaymentIntent(String paymentIntentId) {
        throw new PaymentsNotConfiguredException();
    }

    @Override
    public void refund(String paymentIntentId) {
        throw new PaymentsNotConfiguredException();
    }
}
