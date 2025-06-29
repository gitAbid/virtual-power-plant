package com.challenge.vpp.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@ContextConfiguration(initializers = {AbstractIntegrationTest.Initializer.class})
public abstract class AbstractIntegrationTest {

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17.5")
            .withDatabaseName("test-vpp")
            .withUsername("test")
            .withPassword("test");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            String r2dbcUrl = String.format("r2dbc:postgresql://%s:%d/%s",
                    postgreSQLContainer.getHost(),
                    postgreSQLContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
                    postgreSQLContainer.getDatabaseName());

            TestPropertyValues.of(
                    "spring.r2dbc.url=" + r2dbcUrl,
                    "spring.r2dbc.username=" + postgreSQLContainer.getUsername(),
                    "spring.r2dbc.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
