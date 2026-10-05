package restaurante.team3.giacobello.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.stripe.StripeClient;

import restaurante.team3.giacobello.payments.gateway.StripeApiPaymentGateway;
import restaurante.team3.giacobello.payments.gateway.StripePaymentGateway;
import restaurante.team3.giacobello.payments.gateway.UnconfiguredPaymentGateway;

@Configuration
public class StripeConfig {

    @Bean
    StripePaymentGateway stripePaymentGateway(@Value("${stripe.secret-key:}") String secretKey) {
        if (!StringUtils.hasText(secretKey)) {
            return new UnconfiguredPaymentGateway();
        }
        return new StripeApiPaymentGateway(new StripeClient(secretKey));
    }
}
