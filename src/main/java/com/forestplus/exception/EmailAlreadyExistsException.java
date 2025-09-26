package com.forestplus.exception;

public class EmailAlreadyExistsException extends ForestPlusException {
    public EmailAlreadyExistsException(String email) {
        super("EMAIL_ALREADY_EXISTS", 400);
    }
}
