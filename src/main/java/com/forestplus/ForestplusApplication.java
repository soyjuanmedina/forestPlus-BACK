package com.forestplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.forestplus")
public class ForestplusApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForestplusApplication.class, args);
    }

}
