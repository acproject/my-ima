package com.owiseman.core.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.owiseman.App;
import com.owiseman.core.domain.Permission;
import com.owiseman.core.domain.Realm;
import com.owiseman.core.domain.Role;
import com.owiseman.core.jooq.repository.PermissionRepository;
import com.owiseman.core.jooq.repository.RealmRepository;
import com.owiseman.core.jooq.repository.RoleRepository;
import com.owiseman.core.web.dto.RealmDTO;
import com.owiseman.core.web.dto.RegisterRequest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Testcontainers
@SpringBootTest(classes = App.class)
@ActiveProfiles("prod")
class ProjectCapabilitiesE2ETest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("my_ima_test")
        .withUsername("postgres")
        .withPassword("postgres");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jooq.sql-dialect", () -> "POSTGRES");
        registry.add("spring.liquibase.enabled", () -> true);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/migration/master.xml");
    }

    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mockMvc;

    final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    @Autowired
    RealmRepository realmRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Test
    void fullStackSmokeTest() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk());

        Realm seedRealm = new Realm();
        seedRealm.setName("seed-realm-" + UUID.randomUUID());
        seedRealm.setEnabled(true);
        Realm createdSeedRealm = realmRepository.create(seedRealm);

        Role role = new Role();
        role.setRealmId(createdSeedRealm.getId());
        role.setName("role-" + UUID.randomUUID());
        role.setDescription("e2e");
        role = roleRepository.create(role);

        Permission permission = new Permission();
        permission.setRealmId(createdSeedRealm.getId());
        permission.setResource("users");
        permission.setAction("read");
        permission = permissionRepository.create(permission);

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("user_" + UUID.randomUUID().toString().replace("-", ""));
        registerRequest.setEmail("u_" + UUID.randomUUID().toString().replace("-", "") + "@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRealmId(createdSeedRealm.getId());

        String registerJson = objectMapper.writeValueAsString(registerRequest);
        String registerResponseJson = mockMvc.perform(
                post("/api/auth/register")
                    .contentType("application/json")
                    .content(registerJson)
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode authNode = objectMapper.readTree(registerResponseJson);
        String accessToken = authNode.get("accessToken").asText();
        UUID userId = UUID.fromString(authNode.get("userId").asText());
        assertNotNull(accessToken);
        assertNotNull(userId);
        String authorization = "Bearer " + accessToken;

        RealmDTO newRealm = new RealmDTO();
        newRealm.setName("api-realm-" + UUID.randomUUID());
        newRealm.setEnabled(true);

        String createRealmJson = objectMapper.writeValueAsString(newRealm);
        String createRealmResponseJson = mockMvc.perform(
                post("/api/realms")
                    .header("Authorization", authorization)
                    .contentType("application/json")
                    .content(createRealmJson)
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
        JsonNode createdRealmNode = objectMapper.readTree(createRealmResponseJson);
        UUID createdRealmId = UUID.fromString(createdRealmNode.get("id").asText());
        assertNotNull(createdRealmId);

        String listRealmsResponseJson = mockMvc.perform(
                get("/api/realms")
                    .param("page", "0")
                    .param("size", "50")
                    .header("Authorization", authorization)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        JsonNode realmsNode = objectMapper.readTree(listRealmsResponseJson);
        boolean foundSeedRealm = false;
        for (JsonNode realmNode : realmsNode) {
            UUID realmId = UUID.fromString(realmNode.get("id").asText());
            if (createdSeedRealm.getId().equals(realmId)) {
                foundSeedRealm = true;
                break;
            }
        }
        assertTrue(foundSeedRealm);

        mockMvc.perform(
                post("/api/roles/{roleId}/permissions/{permissionId}", role.getId(), permission.getId())
                    .header("Authorization", authorization)
            )
            .andExpect(status().isOk());

        mockMvc.perform(
                post("/api/users/{userId}/roles/{roleId}", userId, role.getId())
                    .header("Authorization", authorization)
            )
            .andExpect(status().isOk());

        String permissionsResponseJson = mockMvc.perform(
                get("/api/users/{userId}/permissions", userId)
                    .header("Authorization", authorization)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        List<String> permissions = objectMapper.readValue(permissionsResponseJson, new TypeReference<>() {});
        assertTrue(permissions.contains("users:read"));

        String hasPermissionResponseJson = mockMvc.perform(
                get("/api/users/{userId}/permissions/{permission}", userId, "users:read")
                    .header("Authorization", authorization)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        Boolean hasPermission = objectMapper.readValue(hasPermissionResponseJson, Boolean.class);
        assertEquals(Boolean.TRUE, hasPermission);
    }
}
