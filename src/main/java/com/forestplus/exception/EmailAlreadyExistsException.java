package com.forestplus.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Email ya registrado: " + email);
    }
}
