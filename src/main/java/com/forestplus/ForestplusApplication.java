package com.forestplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication(scanBasePackages = "com.forestplus")
public class ForestplusApplication {

    public static void main(String[] args) {
        // Cargar .env sólo si existe (para local)
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir")) // raíz del proyecto
                .ignoreIfMissing()
                .load();

        // Poner variables en System properties si no existen (para que Spring las use)
        if (dotenv.get("DATABASE_URL") != null) {
            System.setProperty("spring.datasource.url", dotenv.get("DATABASE_URL"));
        }
        if (dotenv.get("DATABASE_USERNAME") != null) {
            System.setProperty("spring.datasource.username", dotenv.get("DATABASE_USERNAME"));
        }
        if (dotenv.get("DATABASE_PASSWORD") != null) {
            System.setProperty("spring.datasource.password", dotenv.get("DATABASE_PASSWORD"));
        }
        if (dotenv.get("JWT_SECRET") != null) {
            System.setProperty("jwt.secret", dotenv.get("JWT_SECRET"));
        }
        if (dotenv.get("JWT_EXPIRATION_MS") != null) {
            System.setProperty("jwt.expiration", dotenv.get("JWT_EXPIRATION_MS"));
        }

        SpringApplication.run(ForestplusApplication.class, args);
    }
}
