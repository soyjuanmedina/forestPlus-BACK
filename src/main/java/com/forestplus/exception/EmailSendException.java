package com.forestplus.exception;

public class EmailSendException extends RuntimeException {

    public EmailSendException(String email) {
        super("Email ya registrado: " + email);
    }
}
