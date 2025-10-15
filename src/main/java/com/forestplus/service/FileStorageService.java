package com.forestplus.service;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Guarda un archivo en el sistema de almacenamiento y devuelve su URL pública o ruta accesible.
     *
     * @param file archivo a guardar
     * @param folder subcarpeta donde se almacenará (por ejemplo "users" o "companies")
     * @param id id con el que se guardará el archivo
     * @return ruta o URL de acceso al archivo
     */
    String storeFile(MultipartFile file, String subdirectory, Long id);

    /**
     * Elimina un archivo si es necesario.
     */
    void deleteFile(String path);
    
    String getFileUrl(String filename);
}
