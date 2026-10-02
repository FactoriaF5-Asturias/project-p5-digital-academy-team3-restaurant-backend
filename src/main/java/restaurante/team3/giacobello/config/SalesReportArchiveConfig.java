package restaurante.team3.giacobello.config;

import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

import restaurante.team3.giacobello.invoices.service.SalesReportArchiveJob;
import restaurante.team3.giacobello.invoices.service.SalesReportPdfService;
import restaurante.team3.giacobello.storage.SupabaseStorageClient;
import restaurante.team3.giacobello.storage.SupabaseStorageProperties;

@Configuration
@EnableScheduling
@ConditionalOnExpression("!'${supabase.url:}'.isBlank()")
@EnableConfigurationProperties(SupabaseStorageProperties.class)
public class SalesReportArchiveConfig {

    @Bean
    SupabaseStorageClient supabaseStorageClient(SupabaseStorageProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(30));
        return new SupabaseStorageClient(RestClient.builder().requestFactory(requestFactory), properties);
    }

    @Bean
    SalesReportArchiveJob salesReportArchiveJob(
            SalesReportPdfService salesReportPdfService,
            SupabaseStorageClient supabaseStorageClient) {
        return new SalesReportArchiveJob(
                salesReportPdfService,
                supabaseStorageClient,
                Clock.system(ZoneId.of("Europe/Madrid")));
    }
}
