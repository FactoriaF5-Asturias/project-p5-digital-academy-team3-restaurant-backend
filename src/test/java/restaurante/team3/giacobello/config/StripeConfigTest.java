package restaurante.team3.giacobello.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import restaurante.team3.giacobello.payments.gateway.StripeApiPaymentGateway;
import restaurante.team3.giacobello.payments.gateway.StripePaymentGateway;
import restaurante.team3.giacobello.payments.gateway.UnconfiguredPaymentGateway;

class StripeConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(StripeConfig.class);

    @Test
    void usesTheUnconfiguredGatewayWithoutSecretKey() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(StripePaymentGateway.class);
            assertThat(context.getBean(StripePaymentGateway.class))
                    .isInstanceOf(UnconfiguredPaymentGateway.class);
        });
    }

    @Test
    void usesTheUnconfiguredGatewayWhenSecretKeyIsBlank() {
        contextRunner
                .withPropertyValues("stripe.secret-key=  ")
                .run(context -> assertThat(context.getBean(StripePaymentGateway.class))
                        .isInstanceOf(UnconfiguredPaymentGateway.class));
    }

    @Test
    void usesTheStripeGatewayWhenSecretKeyIsSet() {
        contextRunner
                .withPropertyValues("stripe.secret-key=sk_test_123")
                .run(context -> assertThat(context.getBean(StripePaymentGateway.class))
                        .isInstanceOf(StripeApiPaymentGateway.class));
    }
}
