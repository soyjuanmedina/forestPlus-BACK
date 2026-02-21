package com.forestplus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceImplTest {

    private FileStorageServiceImpl fileStorageService;

    @TempDir
    Path tempDir; // directorio temporal para pruebas

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageServiceImpl();
        fileStorageService.baseUploadDir = tempDir.toString();
    }

    @Test
    void shouldStoreValidFile() throws IOException {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        byte[] content = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                content
        );

        fileStorageService.baseUploadDir = System.getProperty("java.io.tmpdir"); // asigna ruta temporal

        String result = fileStorageService.storeFile(file, "users", "123");
        assertTrue(result.contains("users/123.png"));
    }

    @Test
    void shouldThrowOnEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.png", "image/png", new byte[]{});
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fileStorageService.storeFile(file, "users", "123"));
        assertEquals("Archivo vacío", ex.getMessage());
    }

    @Test
    void shouldThrowOnInvalidExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", new byte[]{1});
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fileStorageService.storeFile(file, "users", "123"));
        assertEquals("Tipo de archivo no permitido", ex.getMessage());
    }
}