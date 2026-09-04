package team3.dev.restaurante;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RestauranteApplicationTests extends AbstractIntegrationTest {

	@Autowired
	DataSource dataSource;

	@Test
	void contextLoads() {
	}

	@Test
	void connectsToRealPostgres() throws Exception {
		try (Connection connection = dataSource.getConnection()) {
			String product = connection.getMetaData().getDatabaseProductName();
			assertThat(product).isEqualTo("PostgreSQL");
		}
	}
}
