package com.example.todo.api.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeMethod;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Label;

import java.util.ArrayList;

public class BaseTest {

    @BeforeSuite
    public void configureRestAssured() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        RestAssured.config = RestAssuredConfig.config()
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory((type, s) -> objectMapper));
    }

    @BeforeMethod
    public void attachModuleLabel() {
        // Add a custom label so Allure reports can be grouped by module
        Allure.getLifecycle().updateTestCase(testResult -> {
            if (testResult.getLabels() == null) {
                testResult.setLabels(new ArrayList<>());
            }
            testResult.getLabels().add(new Label().setName("module").setValue("API"));
        });
    }
}
