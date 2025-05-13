package com.example.urlshortener.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * This class is responsible for loading environment variables from a .env file
 * when the application is running in the 'development' profile.
 * It uses the dotenv-java library to load the variables.
 */
@Configuration
@Profile("development")
public class DotenvConfig {

    @Value("${spring.is.test.environment:false}")
    private boolean isTestEnvironment;

    @PostConstruct
    public void loadDevEnv() {
        if (isTestEnvironment) {
            Dotenv dotenv = Dotenv.load();
            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );
            System.out.println("Loaded environment variables from .env (development profile).");
        } else {
            System.out.println("Not loading .env file because current profile is not 'development'.");
        }
    }
}