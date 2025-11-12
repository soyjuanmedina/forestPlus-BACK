package com.forestplus.exception;

public class TreeTypeNotFoundException extends RuntimeException {
    public TreeTypeNotFoundException(Long id) {
        super("Tipo de árbol no encontrado con ID: " + id);
    }
}
