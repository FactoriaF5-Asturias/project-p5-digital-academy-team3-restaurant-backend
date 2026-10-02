package restaurante.team3.giacobello.storage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

class SupabaseStorageClientTest {

    private static final byte[] PDF = { 37, 80, 68, 70 };

    private MockRestServiceServer server;
    private SupabaseStorageClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        SupabaseStorageProperties properties = new SupabaseStorageProperties(
                "https://project.supabase.co",
                "service-key",
                "sales-reports");
        client = new SupabaseStorageClient(builder, properties);
    }

    @Test
    void uploadSendsThePdfToTheBucketWithTheServiceKey() {
        server.expect(requestTo("https://project.supabase.co/storage/v1/object/sales-reports/daily/report.pdf"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer service-key"))
                .andExpect(header("apikey", "service-key"))
                .andExpect(header("x-upsert", "true"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(PDF))
                .andRespond(withSuccess());

        client.upload("daily/report.pdf", PDF, MediaType.APPLICATION_PDF);

        server.verify();
    }

    @Test
    void uploadIgnoresATrailingSlashInTheProjectUrl() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer slashServer = MockRestServiceServer.bindTo(builder).build();
        SupabaseStorageClient slashClient = new SupabaseStorageClient(
                builder,
                new SupabaseStorageProperties("https://project.supabase.co/", "service-key", "sales-reports"));
        slashServer.expect(requestTo("https://project.supabase.co/storage/v1/object/sales-reports/daily/report.pdf"))
                .andRespond(withSuccess());

        slashClient.upload("daily/report.pdf", PDF, MediaType.APPLICATION_PDF);

        slashServer.verify();
    }

    @Test
    void uploadRejectsPathsWithUnsafeCharacters() {
        assertThrows(
                IllegalArgumentException.class,
                () -> client.upload("daily/../report?.pdf", PDF, MediaType.APPLICATION_PDF));
    }

    @Test
    void propertiesDoNotPrintTheServiceKey() {
        SupabaseStorageProperties properties = new SupabaseStorageProperties(
                "https://project.supabase.co",
                "service-key",
                "sales-reports");

        assertFalse(properties.toString().contains("service-key"));
    }

    @Test
    void uploadThrowsWhenSupabaseRejectsTheFile() {
        server.expect(requestTo("https://project.supabase.co/storage/v1/object/sales-reports/daily/report.pdf"))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        assertThrows(
                RestClientException.class,
                () -> client.upload("daily/report.pdf", PDF, MediaType.APPLICATION_PDF));
    }
}
