package com.forestplus.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/uploads")
public class UploadsController {

    @Value("${file.upload-dir}")
    private String baseUploadDir;

    @GetMapping("/users/{filename:.+}")
    public ResponseEntity<Resource> serveUserImage(@PathVariable String filename) throws MalformedURLException {
        System.out.println("[UploadsController#serveUserImage] Petición recibida para: " + filename);

        Path file = Paths.get(baseUploadDir, "users", filename);
        Resource resource = new UrlResource(file.toUri());
        if(!resource.exists() || !resource.isReadable()) {
            System.out.println("[UploadsController#serveUserImage] Archivo no encontrado o no legible: " + file.toAbsolutePath());
            return ResponseEntity.notFound().build();
        }

        System.out.println("[UploadsController#serveUserImage] Sirviendo archivo: " + file.toAbsolutePath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/png")
                .body(resource);
    }
    
    @GetMapping("/companies/{filename:.+}")
    public ResponseEntity<Resource> serveCompanyImage(@PathVariable String filename) throws MalformedURLException {
        System.out.println("[UploadsController#serveCompanyImage] Petición recibida para: " + filename);

        Path file = Paths.get(baseUploadDir, "companies", filename);
        Resource resource = new UrlResource(file.toUri());
        if(!resource.exists() || !resource.isReadable()) {
            System.out.println("[UploadsController#serveCompanyImage] Archivo no encontrado o no legible: " + file.toAbsolutePath());
            return ResponseEntity.notFound().build();
        }

        System.out.println("[UploadsController#serveCompanyImage] Sirviendo archivo: " + file.toAbsolutePath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/png")
                .body(resource);
    }
}
