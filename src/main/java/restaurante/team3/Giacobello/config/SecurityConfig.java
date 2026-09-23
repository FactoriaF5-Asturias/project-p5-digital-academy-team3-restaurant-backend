package restaurante.team3.Giacobello.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            Environment environment,
            @Value("${api-endpoint}") String apiEndpoint) throws Exception {

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        boolean devProfile = environment.acceptsProfiles(Profiles.of("dev"));
        http.authorizeHttpRequests(auth -> {
            auth
                    .requestMatchers("/images/**", "/error").permitAll()
                    .requestMatchers(HttpMethod.GET, apiEndpoint + "/products").permitAll()
                    .requestMatchers(HttpMethod.GET, apiEndpoint + "/products/*").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers(HttpMethod.GET, apiEndpoint + "/orders", apiEndpoint + "/orders/*").permitAll()
                    .requestMatchers(HttpMethod.GET, apiEndpoint + "/tablets").permitAll()
                    .requestMatchers(HttpMethod.GET, apiEndpoint + "/tablets/*").permitAll()
                    .requestMatchers(HttpMethod.GET, apiEndpoint + "/categories").permitAll()
                    .requestMatchers(HttpMethod.GET, apiEndpoint + "/categories/*").permitAll()
                    .requestMatchers(HttpMethod.GET, apiEndpoint + "/invoices").permitAll()
                    .requestMatchers(HttpMethod.GET, apiEndpoint + "/paymentmethod").permitAll();
            if (devProfile) {
                auth.requestMatchers(
                        HttpMethod.POST,
                        apiEndpoint + "/orders").permitAll();
                auth.requestMatchers(
                        HttpMethod.GET,
                        apiEndpoint + "/orders/*/invoice").permitAll();
                auth.requestMatchers(
                        HttpMethod.PUT,
                        apiEndpoint + "/orders/*/status").permitAll();
            }
            auth.anyRequest().authenticated();
        });

        if (devProfile) {
            http.csrf(csrf -> csrf.ignoringRequestMatchers(request -> {
                String method = request.getMethod();
                String path = request.getServletPath();

                boolean isCreateOrder = "POST".equals(method)
                        && (apiEndpoint + "/orders").equals(path);

                boolean isUpdateOrderStatus = "PUT".equals(method)
                        && path.startsWith(apiEndpoint + "/orders/")
                        && path.endsWith("/status");

                return isCreateOrder || isUpdateOrderStatus;
            }));
        }
        return http.build();
    }
}
