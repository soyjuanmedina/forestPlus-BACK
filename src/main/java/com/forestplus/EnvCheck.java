package com.forestplus;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvCheck {

    @Value("${DATABASE_URL:NOT_SET}")
    private String dbUrl;

    @Value("${DATABASE_USERNAME:NOT_SET}")
    private String dbUser;

    @Value("${DATABASE_PASSWORD:NOT_SET}")
    private String dbPass;

    @PostConstruct
    public void checkEnv() {
        System.out.println("DATABASE_URL = " + dbUrl);
        System.out.println("DATABASE_USERNAME = " + dbUser);
        System.out.println("DATABASE_PASSWORD = " + (dbPass.equals("NOT_SET") ? "NOT_SET" : "SET"));
    }
}
