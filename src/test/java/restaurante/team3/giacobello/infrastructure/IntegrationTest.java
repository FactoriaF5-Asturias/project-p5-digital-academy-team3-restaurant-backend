package restaurante.team3.giacobello.infrastructure;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.ActiveProfiles;

import java.security.SecureRandom;
import java.util.Base64;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public abstract class IntegrationTest {

	private static final String TEST_JWT_KEY = Base64.getEncoder()
			.encodeToString(new SecureRandom().generateSeed(64));

	@DynamicPropertySource
	static void jwtProperties(DynamicPropertyRegistry registry) {
		registry.add("jwt.key", () -> TEST_JWT_KEY);
	}
}
