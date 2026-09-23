package restaurante.team3.Giacobello.infrastructure;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainers.class)
public abstract class IntegrationTest {
}