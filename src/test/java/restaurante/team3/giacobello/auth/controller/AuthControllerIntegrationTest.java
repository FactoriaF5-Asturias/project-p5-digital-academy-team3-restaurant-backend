package restaurante.team3.giacobello.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import restaurante.team3.giacobello.infrastructure.IntegrationTest;

class AuthControllerIntegrationTest extends IntegrationTest {

    private static final String PASSWORD = "correct-password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String username;

    @BeforeEach
    void createUser() {
        username = "auth-test-" + UUID.randomUUID();
        Integer userId = jdbcTemplate.queryForObject(
                "INSERT INTO users_auth (username, password_hash) VALUES (?, ?) RETURNING id",
                Integer.class,
                username,
                passwordEncoder.encode(PASSWORD));
        jdbcTemplate.update(
                "INSERT INTO users_details (user_id, email, role_id) "
                        + "SELECT ?, ?, id FROM users_role WHERE name = 'KITCHEN'",
                userId,
                username + "@example.test");
    }

    @Test
    void validCredentialsReturnJwtWithRoleClaim() throws Exception {
        MvcResult result = login(PASSWORD)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
        String token = extractToken(result);

        org.junit.jupiter.api.Assertions.assertEquals(
                List.of("KITCHEN"), jwtDecoder.decode(token).getClaimAsStringList("roles"));
    }

    @Test
    void incorrectPasswordIsUnauthorized() throws Exception {
        login("wrong-password").andExpect(status().isUnauthorized());
    }

    @Test
    void unknownUserIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType("application/json")
                        .content(loginJson("missing-user", PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedRouteRequiresValidJwt() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isUnauthorized());

        MvcResult result = login(PASSWORD)
                .andExpect(status().isOk())
                .andReturn();
        String token = extractToken(result);

        mockMvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private org.springframework.test.web.servlet.ResultActions login(String password) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/token")
                .contentType("application/json")
                .content(loginJson(username, password)));
    }

    private String loginJson(String user, String password) throws Exception {
        return objectMapper.writeValueAsString(new LoginPayload(user, password));
    }

    private record LoginPayload(String username, String password) {
    }

    private String extractToken(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}