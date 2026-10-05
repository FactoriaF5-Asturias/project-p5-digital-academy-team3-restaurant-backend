package restaurante.team3.giacobello.payments.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.stripe.StripeClient;
import com.sun.net.httpserver.HttpServer;

import restaurante.team3.giacobello.payments.exceptions.PaymentGatewayException;
import restaurante.team3.giacobello.payments.exceptions.PaymentNotFoundException;

class StripeApiPaymentGatewayTest {

    private HttpServer server;
    private StripeApiPaymentGateway gateway;
    private final AtomicReference<String> lastRequestBody = new AtomicReference<>();
    private final AtomicReference<String> lastRequestPath = new AtomicReference<>();

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.start();
        StripeClient client = StripeClient.builder()
                .setApiKey("sk_test_123")
                .setApiBase("http://127.0.0.1:" + server.getAddress().getPort())
                .setMaxNetworkRetries(0)
                .build();
        gateway = new StripeApiPaymentGateway(client);
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    private void respond(int status, String json) {
        server.createContext("/", exchange -> {
            lastRequestPath.set(exchange.getRequestURI().getPath());
            lastRequestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
    }

    @Test
    void createPaymentIntentSendsAmountCurrencyAndCardOnly() {
        respond(200, "{\"id\":\"pi_1\",\"object\":\"payment_intent\",\"client_secret\":\"pi_1_secret_x\"}");

        PaymentIntentCreated created = gateway.createPaymentIntent(2550, "eur");

        assertEquals("pi_1", created.id());
        assertEquals("pi_1_secret_x", created.clientSecret());
        assertEquals("/v1/payment_intents", lastRequestPath.get());
        assertTrue(lastRequestBody.get().contains("amount=2550"));
        assertTrue(lastRequestBody.get().contains("currency=eur"));
        assertTrue(lastRequestBody.get().contains("payment_method_types"));
    }

    @Test
    void retrievePaymentIntentMapsTheStripeResponse() {
        respond(200, "{\"id\":\"pi_1\",\"object\":\"payment_intent\",\"status\":\"succeeded\","
                + "\"amount\":2550,\"currency\":\"eur\"}");

        PaymentIntentDetails details = gateway.retrievePaymentIntent("pi_1");

        assertEquals(new PaymentIntentDetails("pi_1", "succeeded", 2550, "eur"), details);
        assertEquals("/v1/payment_intents/pi_1", lastRequestPath.get());
    }

    @Test
    void createPaymentIntentWrapsStripeErrors() {
        respond(401, "{\"error\":{\"type\":\"invalid_request_error\",\"message\":\"Invalid API Key\"}}");

        PaymentGatewayException ex = assertThrows(PaymentGatewayException.class,
                () -> gateway.createPaymentIntent(2550, "eur"));

        assertEquals("No se pudo contactar con el proveedor de pagos", ex.getMessage());
    }

    @Test
    void retrievePaymentIntentMapsUnknownIntentsToNotFound() {
        respond(404, "{\"error\":{\"type\":\"invalid_request_error\",\"code\":\"resource_missing\","
                + "\"message\":\"No such payment_intent\"}}");

        assertThrows(PaymentNotFoundException.class, () -> gateway.retrievePaymentIntent("pi_x"));
    }

    @Test
    void retrievePaymentIntentWrapsStripeApiErrors() {
        respond(500, "{\"error\":{\"type\":\"api_error\",\"message\":\"boom\"}}");

        assertThrows(PaymentGatewayException.class, () -> gateway.retrievePaymentIntent("pi_x"));
    }

    @Test
    void refundCreatesARefundForThePaymentIntent() {
        respond(200, "{\"id\":\"re_1\",\"object\":\"refund\",\"status\":\"succeeded\"}");

        gateway.refund("pi_1");

        assertEquals("/v1/refunds", lastRequestPath.get());
        assertTrue(lastRequestBody.get().contains("payment_intent=pi_1"));
    }

    @Test
    void refundWrapsStripeErrors() {
        respond(500, "{\"error\":{\"type\":\"api_error\",\"message\":\"boom\"}}");

        assertThrows(PaymentGatewayException.class, () -> gateway.refund("pi_1"));
    }
}
