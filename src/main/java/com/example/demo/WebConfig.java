package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Carpeta donde se guardan las imágenes (viene de application.properties)
    @Value("${app.uploads.dir:uploads}")
    private String carpeta;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String ruta = Paths.get(carpeta).toAbsolutePath().normalize().toUri().toString();
        if (!ruta.endsWith("/")) ruta += "/";
        registry.addResourceHandler("/uploads/**").addResourceLocations(ruta);
    }
}