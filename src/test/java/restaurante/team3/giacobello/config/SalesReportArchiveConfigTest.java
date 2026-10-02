package restaurante.team3.giacobello.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import restaurante.team3.giacobello.invoices.service.SalesReportArchiveJob;
import restaurante.team3.giacobello.invoices.service.SalesReportPdfService;
import restaurante.team3.giacobello.storage.SupabaseStorageClient;
import restaurante.team3.giacobello.storage.SupabaseStorageProperties;

class SalesReportArchiveConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(SalesReportPdfService.class, () -> mock(SalesReportPdfService.class))
            .withUserConfiguration(SalesReportArchiveConfig.class);

    @Test
    void doesNotCreateTheArchiveJobWithoutSupabaseUrl() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(SalesReportArchiveJob.class);
            assertThat(context).doesNotHaveBean(SupabaseStorageClient.class);
        });
    }

    @Test
    void doesNotCreateTheArchiveJobWhenSupabaseUrlIsEmpty() {
        contextRunner
                .withPropertyValues("supabase.url=", "supabase.service-key=")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(SalesReportArchiveJob.class);
                });
    }

    @Test
    void createsTheArchiveJobWhenSupabaseIsConfigured() {
        contextRunner
                .withPropertyValues(
                        "supabase.url=https://project.supabase.co",
                        "supabase.service-key=service-key")
                .run(context -> {
                    assertThat(context).hasSingleBean(SalesReportArchiveJob.class);
                    assertThat(context).hasSingleBean(SupabaseStorageClient.class);
                    assertThat(context.getBean(SupabaseStorageProperties.class).reportsBucket())
                            .isEqualTo("sales-reports");
                });
    }

    @Test
    void failsToStartWhenTheServiceKeyIsMissing() {
        contextRunner
                .withPropertyValues("supabase.url=https://project.supabase.co")
                .run(context -> assertThat(context).hasFailed());
    }
}
