package com.example.urlshortener;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UrlshortenerApplication {

	public static void main(String[] args) {
		// since i m using 3rd party .env support, i need to load the .env file in the system (just like we do export var=value) same this does
		// then the loaded variables are used by the application.properties
		Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);
		SpringApplication.run(UrlshortenerApplication.class, args);
	}

}
