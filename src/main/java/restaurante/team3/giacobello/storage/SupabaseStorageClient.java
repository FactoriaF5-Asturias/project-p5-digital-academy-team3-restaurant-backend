package restaurante.team3.giacobello.storage;

import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class SupabaseStorageClient {

    private static final Pattern SAFE_PATH = Pattern.compile("[A-Za-z0-9_-]+(/[A-Za-z0-9_-]+)*\\.[A-Za-z0-9]+");

    private final RestClient restClient;
    private final String bucket;

    public SupabaseStorageClient(RestClient.Builder builder, SupabaseStorageProperties properties) {
        this.restClient = builder
                .baseUrl(properties.url() + "/storage/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.serviceKey())
                .defaultHeader("apikey", properties.serviceKey())
                .build();
        this.bucket = properties.reportsBucket();
    }

    public void upload(String path, byte[] content, MediaType contentType) {
        if (!SAFE_PATH.matcher(path).matches()) {
            throw new IllegalArgumentException("Ruta de fichero no válida: " + path);
        }
        restClient.post()
                .uri("/object/{bucket}/" + path, bucket)
                .contentType(contentType)
                .header("x-upsert", "true")
                .body(content)
                .retrieve()
                .toBodilessEntity();
    }
}
