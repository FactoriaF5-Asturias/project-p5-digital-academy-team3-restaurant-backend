package restaurante.team3.giacobello.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "supabase")
public record SupabaseStorageProperties(
        @NotBlank String url,
        @NotBlank String serviceKey,
        @DefaultValue("sales-reports") @NotBlank String reportsBucket) {

    @Override
    public String toString() {
        return "SupabaseStorageProperties[url=" + url + ", serviceKey=***, reportsBucket=" + reportsBucket + "]";
    }
}
