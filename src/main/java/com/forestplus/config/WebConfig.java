package com.forestplus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
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

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorPathExtension(false)       // desactiva la extensión de archivo (.json, .xml, etc.)
                .favorParameter(false)           // desactiva parámetros tipo ?format=json
                .ignoreAcceptHeader(true)        // ignora el Accept header del cliente (evita que * / * devuelva blob)
                .defaultContentType(MediaType.APPLICATION_JSON); // forzar JSON por defecto
    }
}
