package com.retailordersystem.api;

import static org.hamcrest.Matchers.containsString;

import java.time.Duration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.retailordersystem.api.constants.DockerImageConstants;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailordersystem.model.Order;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Testcontainers
public class OrderApiTest {

    private static final Integer TIMEOUT = 120;
    private static final Logger logger = LoggerFactory.getLogger(OrderApiTest.class);

    @LocalServerPort
    int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse(DockerImageConstants.POSTGRES_IMAGE));

    @BeforeAll
    static void startContainers() {
        Awaitility.await().atMost(Duration.ofSeconds(TIMEOUT)).until(postgres::isRunning);
        logger.info("PostgreSQL is up and running!");
    }

    @AfterAll
    static void stopContainers() {
        if (postgres != null) {
            postgres.stop();
            logger.info("PostgreSQL container stopped.");
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

    }


    @Test
    public void shouldCreateOrder() {

        Order order = new Order("DUMMY_STATUS", "Order from Integration Test");
        String givenBody;
        try {
            givenBody = objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .baseUri("http://localhost").port(port).basePath("/orders")
                .body(givenBody)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(containsString("DUMMY_STATUS"));
    }
}