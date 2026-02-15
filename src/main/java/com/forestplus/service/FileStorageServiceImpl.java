// FileStorageServiceImpl.java
package com.forestplus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String baseUploadDir;
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("png", "jpg", "jpeg", "webp");

    /**
     * Guardar archivo usando UUID del usuario
     * @param file archivo a guardar
     * @param subdirectory subcarpeta (ej: "users")
     * @param userUuid UUID del usuario
     * @return nombre del archivo guardado
     */
    public String storeFile(MultipartFile file, String subdirectory, String idOrUuid ) {
        System.out.println("[FileStorageServiceImpl#storeFile] Ruta base de uploads: " + baseUploadDir);
        if (file.isEmpty()) {
            throw new RuntimeException("Archivo vacío");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Tipo de archivo no permitido");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new RuntimeException("Nombre de archivo inválido");
        }

        String extension = originalFilename
                .substring(originalFilename.lastIndexOf('.') + 1)
                .toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("Extensión no permitida");
        }
        try {
            Path uploadPath = Paths.get(baseUploadDir, subdirectory);
            Files.createDirectories(uploadPath);

            String filename = idOrUuid + "." + extension;
            Path filePath = uploadPath.resolve(filename).normalize();
            if (!filePath.startsWith(uploadPath)) {
                throw new RuntimeException("Ruta inválida");
            }
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            BufferedImage image = ImageIO.read(filePath.toFile());
            if (image == null) {
                throw new RuntimeException("Archivo no es una imagen válida");
            }

            // Devolver la ruta relativa que el front usará
            return Paths.get("api/uploads", subdirectory, filename).toString().replace("\\", "/");
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        }
    }

    @Override
    public String getFileUrl(String filename) {
        return "/uploads/" + filename; 
    }

    @Override
    public void deleteFile(String filename) {
        try {
            Path filePath = Paths.get(baseUploadDir, filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar el archivo", e);
        }
    }

}
