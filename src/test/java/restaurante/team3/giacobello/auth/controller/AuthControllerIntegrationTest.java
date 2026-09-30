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
import org.springframework.transaction.annotation.Transactional;

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

        private final ObjectMapper objectMapper = new ObjectMapper();

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
                mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());

        MvcResult result = login(PASSWORD)
                .andExpect(status().isOk())
                .andReturn();
        String token = extractToken(result);

        mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void anonymousUserCanBrowseAndPlaceOrderButNotAccessRestrictedResources() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/tablets"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/paymentmethod"))
                .andExpect(status().isOk());

        String orderRequest = """
                {
                  "tabletId": 3,
                  "orderTypeName": "DINE IN",
                  "paymentMethodName": "CASH",
                  "items": [
                    { "productId": 1, "quantity": 2 },
                    { "productId": 8, "quantity": 1 }
                  ]
                }
                """;
        mockMvc.perform(post("/api/v1/orders")
                        .contentType("application/json")
                        .content(orderRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/orders/1/invoice"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void kitchenRoleCanReadOrders() throws Exception {
        String token = extractToken(login(PASSWORD).andReturn());

        mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void kitchenRoleCannotManageCategories() throws Exception {
        String token = extractToken(login(PASSWORD).andReturn());

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("{\"name\":\"restricted-category\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminRoleCanManageCategories() throws Exception {
        String adminUsername = createUserWithRole("ADMIN");
        String token = extractToken(login(adminUsername, PASSWORD).andReturn());

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("{\"name\":\"admin-" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isCreated());
    }

        @Test
        void kitchenRoleCannotModifyProducts() throws Exception {
                String token = extractToken(login(PASSWORD).andReturn());

                mockMvc.perform(post("/api/v1/products")
                                                .header("Authorization", "Bearer " + token)
                                                .contentType("application/json")
                                                .content("{}"))
                                .andExpect(status().isForbidden());
        }

        @Test
        void adminRoleCanReachProductManagement() throws Exception {
                String adminUsername = createUserWithRole("ADMIN");
                String token = extractToken(login(adminUsername, PASSWORD).andReturn());

                mockMvc.perform(post("/api/v1/products")
                                                .header("Authorization", "Bearer " + token)
                                                .contentType("application/json")
                                                .content("{}"))
                                .andExpect(status().isBadRequest());
        }

    @Test
        @Transactional
    void userWithoutRoleCanPlaceOrderButCannotAccessRestrictedApis() throws Exception {
        String userWithoutRole = "auth-test-" + UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO users_auth (username, password_hash) VALUES (?, ?)",
                userWithoutRole,
                passwordEncoder.encode(PASSWORD));

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/token")
                        .contentType("application/json")
                        .content(loginJson(userWithoutRole, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();
        String token = extractToken(loginResult);
        org.junit.jupiter.api.Assertions.assertEquals(
                List.of(), jwtDecoder.decode(token).getClaimAsStringList("roles"));

        String orderRequest = """
                {
                  "tabletId": 3,
                  "orderTypeName": "DINE IN",
                  "paymentMethodName": "CASH",
                  "items": [
                    { "productId": 1, "quantity": 2 },
                    { "productId": 8, "quantity": 1 }
                  ]
                }
                """;
        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(orderRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    private org.springframework.test.web.servlet.ResultActions login(String password) throws Exception {
        return login(username, password);
    }

    private org.springframework.test.web.servlet.ResultActions login(String user, String password) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/token")
                .contentType("application/json")
                .content(loginJson(user, password)));
    }

    private String createUserWithRole(String role) {
        String newUsername = "auth-test-" + UUID.randomUUID();
        Integer userId = jdbcTemplate.queryForObject(
                "INSERT INTO users_auth (username, password_hash) VALUES (?, ?) RETURNING id",
                Integer.class,
                newUsername,
                passwordEncoder.encode(PASSWORD));
        jdbcTemplate.update(
                "INSERT INTO users_details (user_id, email, role_id) "
                        + "SELECT ?, ?, id FROM users_role WHERE name = ?",
                userId,
                newUsername + "@example.test",
                role);
        return newUsername;
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