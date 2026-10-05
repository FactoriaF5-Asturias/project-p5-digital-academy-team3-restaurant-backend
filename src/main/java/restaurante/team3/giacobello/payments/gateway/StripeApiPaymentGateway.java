package restaurante.team3.giacobello.payments.gateway;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import restaurante.team3.giacobello.payments.exceptions.PaymentGatewayException;

public class StripeApiPaymentGateway implements StripePaymentGateway {

    private final StripeClient stripeClient;

    public StripeApiPaymentGateway(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    @Override
    public PaymentIntentCreated createPaymentIntent(long amountInCents, String currency) {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency)
                .addPaymentMethodType("card")
                .build();
        try {
            PaymentIntent intent = stripeClient.v1().paymentIntents().create(params);
            return new PaymentIntentCreated(intent.getId(), intent.getClientSecret());
        } catch (StripeException e) {
            throw new PaymentGatewayException(e);
        }
    }

    @Override
    public PaymentIntentDetails retrievePaymentIntent(String paymentIntentId) {
        try {
            PaymentIntent intent = stripeClient.v1().paymentIntents().retrieve(paymentIntentId);
            return new PaymentIntentDetails(
                    intent.getId(),
                    intent.getStatus(),
                    intent.getAmount(),
                    intent.getCurrency());
        } catch (StripeException e) {
            throw new PaymentGatewayException(e);
        }
    }
}
