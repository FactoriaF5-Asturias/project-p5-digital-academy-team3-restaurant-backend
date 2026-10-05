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

    private static final int CONNECT_TIMEOUT_MILLIS = 3000;
    private static final int READ_TIMEOUT_MILLIS = 10000;
    private static final int MAX_NETWORK_RETRIES = 1;

    @Bean
    StripePaymentGateway stripePaymentGateway(@Value("${stripe.secret-key:}") String secretKey) {
        if (!StringUtils.hasText(secretKey)) {
            return new UnconfiguredPaymentGateway();
        }
        return new StripeApiPaymentGateway(buildClient(secretKey));
    }

    private StripeClient buildClient(String secretKey) {
        return StripeClient.builder()
                .setApiKey(secretKey)
                .setConnectTimeout(CONNECT_TIMEOUT_MILLIS)
                .setReadTimeout(READ_TIMEOUT_MILLIS)
                .setMaxNetworkRetries(MAX_NETWORK_RETRIES)
                .build();
    }
}
