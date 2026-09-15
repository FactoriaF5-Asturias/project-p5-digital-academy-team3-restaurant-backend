package restaurante.team3.Giacobello.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Value("${api-endpoint}") String apiEndpoint) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/images/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, apiEndpoint + "/products").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, apiEndpoint + "/orders").permitAll()
                        .requestMatchers(HttpMethod.GET, apiEndpoint + "/tablets").permitAll()
                        .requestMatchers(HttpMethod.GET, apiEndpoint + "/tablets/*").permitAll()
                        .requestMatchers(HttpMethod.GET, apiEndpoint + "/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, apiEndpoint + "/categories/*").permitAll()
                        .anyRequest().authenticated());
        return http.build();
    }
}
