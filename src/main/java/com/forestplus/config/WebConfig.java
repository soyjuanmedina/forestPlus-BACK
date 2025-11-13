package com.forestplus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations("file:/appservers/forestplus-files/uploads/")
                .setCachePeriod(3600); // opcional: cache de 1 hora
    }
}
