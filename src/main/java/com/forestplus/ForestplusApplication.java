package com.forestplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication(scanBasePackages = "com.forestplus")
public class ForestplusApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory(".") // ruta de tu .env
                .ignoreIfMissing()
                .load();

// Poner variables en System properties para que Spring las use
System.setProperty("spring.datasource.url", dotenv.get("DATABASE_URL"));
System.setProperty("spring.datasource.username", dotenv.get("DATABASE_USERNAME"));
System.setProperty("spring.datasource.password", dotenv.get("DATABASE_PASSWORD"));

// JWT
System.setProperty("jwt.secret", dotenv.get("JWT_SECRET"));
System.setProperty("jwt.expiration", dotenv.get("JWT_EXPIRATION_MS"));

SpringApplication.run(ForestplusApplication.class, args);
	}

}
