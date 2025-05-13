package com.example.urlshortener.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * This class is config class which is loaded as soon as the application context is initialized.
 * use this for test or debugging purpose.
 * its post construct is used to automatically call this function after the bean is created.
 */
@Configuration
public class TestConfig {

    @PostConstruct
    public void logDataSourceUrl() {
        System.out.println("--- TestConfig log printed ---");
    }
}