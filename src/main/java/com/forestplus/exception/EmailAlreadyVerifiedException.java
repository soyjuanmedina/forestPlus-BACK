package com.forestplus.exception;

public class EmailAlreadyVerifiedException extends ForestPlusException {
    public EmailAlreadyVerifiedException(String message) {
        super(message, 400);
    }
}
