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

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@RestController
@RequestMapping("/api/uploads")
public class UploadsController {

    @Value("${file.upload-dir}")
    private String baseUploadDir;
    
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("png", "jpg", "jpeg", "webp");

    @GetMapping("/users/{filename:.+}")
    public ResponseEntity<Resource> serveUserImage(@PathVariable String filename) throws MalformedURLException {
        System.out.println("[UploadsController#serveUserImage] Petición recibida para: " + filename);
        
        Path file = resolveAndValidatePath("users", filename);
        Resource resource = new UrlResource(file.toUri());
        
        if(!resource.exists() || !resource.isReadable()) {
            System.out.println("[UploadsController#serveUserImage] Archivo no encontrado o no legible: " + file.toAbsolutePath());
            return ResponseEntity.notFound().build();
        }
        
        String contentType = detectContentType(file);
        
        if (!contentType.startsWith("image/")) {
            return ResponseEntity.status(403).build();
        }

        System.out.println("[UploadsController#serveUserImage] Sirviendo archivo: " + file.toAbsolutePath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(resource);
    }
    
    @GetMapping("/companies/{filename:.+}")
    public ResponseEntity<Resource> serveCompanyImage(@PathVariable String filename) throws MalformedURLException {
        System.out.println("[UploadsController#serveCompanyImage] Petición recibida para: " + filename);
        
        Path file = resolveAndValidatePath("companies", filename);
        Resource resource = new UrlResource(file.toUri());
        
        
        if(!resource.exists() || !resource.isReadable()) {
            System.out.println("[UploadsController#serveCompanyImage] Archivo no encontrado o no legible: " + file.toAbsolutePath());
            return ResponseEntity.notFound().build();
        }
        
        String contentType = detectContentType(file);
        
        if (!contentType.startsWith("image/")) {
            return ResponseEntity.status(403).build();
        }

        System.out.println("[UploadsController#serveCompanyImage] Sirviendo archivo: " + file.toAbsolutePath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(resource);
    }
    
    private Path resolveAndValidatePath(String subdir, String filename) {
        if (filename.contains("..")) {
            throw new RuntimeException("[UploadsController#resolveAndValidatePath] Path traversal detectado");
        }

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("[UploadsController#resolveAndValidatePath] Extensión no permitida");
        }

        Path file = Paths.get(baseUploadDir, subdir).resolve(filename).normalize();

        if (!file.startsWith(Paths.get(baseUploadDir))) {
            throw new RuntimeException("[UploadsController#resolveAndValidatePath] Ruta inválida");
        }

        return file;
    }
    
    private String detectContentType(Path file) {
        try {
            return Files.probeContentType(file);
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
}
