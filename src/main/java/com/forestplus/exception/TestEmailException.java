package com.forestplus.exception;

public class TestEmailException {
    public static void main(String[] args) {
        try {
            throw new EmailAlreadyExistsException("test@example.com");
        } catch (EmailAlreadyExistsException e) {
            System.out.println("Excepción reconocida correctamente: " + e.getMessage());
            System.out.println("Status: " + e.getStatus());
        }
    }
}