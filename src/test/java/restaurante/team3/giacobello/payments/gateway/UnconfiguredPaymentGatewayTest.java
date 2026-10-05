package restaurante.team3.giacobello.payments.gateway;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import restaurante.team3.giacobello.payments.exceptions.PaymentsNotConfiguredException;

class UnconfiguredPaymentGatewayTest {

    private final UnconfiguredPaymentGateway gateway = new UnconfiguredPaymentGateway();

    @Test
    void createPaymentIntentFailsAsNotConfigured() {
        assertThrows(PaymentsNotConfiguredException.class,
                () -> gateway.createPaymentIntent(1000, "eur"));
    }

    @Test
    void retrievePaymentIntentFailsAsNotConfigured() {
        assertThrows(PaymentsNotConfiguredException.class,
                () -> gateway.retrievePaymentIntent("pi_1"));
    }
}
