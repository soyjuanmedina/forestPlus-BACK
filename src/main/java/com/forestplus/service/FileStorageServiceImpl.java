// FileStorageServiceImpl.java
package com.forestplus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String baseUploadDir;

    /**
     * Guardar archivo usando UUID del usuario
     * @param file archivo a guardar
     * @param subdirectory subcarpeta (ej: "users")
     * @param userUuid UUID del usuario
     * @return nombre del archivo guardado
     */
    public String storeFile(MultipartFile file, String subdirectory, Long id) {
        System.out.println("[FileStorageServiceImpl#storeFile] Ruta base de uploads: " + baseUploadDir);
        try {
            Path uploadPath = Paths.get(baseUploadDir, subdirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String filename = id + extension;
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

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
