package restaurante.team3.giacobello.payments.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.AuthenticationException;

class PaymentGatewayExceptionTest {

    @Test
    void exposesTheStripeErrorCodeAndTypeWithoutTheCauseMessage() {
        PaymentGatewayException ex = new PaymentGatewayException(
                new AuthenticationException("sensitive detail", "req_1", "invalid_api_key", 401));

        assertEquals("AuthenticationException", ex.getCauseType());
        assertEquals("invalid_api_key", ex.getStripeCode());
        assertEquals("No se pudo contactar con el proveedor de pagos", ex.getMessage());
    }

    @Test
    void hasNoStripeCodeForNonStripeCauses() {
        PaymentGatewayException ex = new PaymentGatewayException(new ApiConnectionException("down"));

        assertEquals("ApiConnectionException", ex.getCauseType());
        assertNull(ex.getStripeCode());
    }
}
