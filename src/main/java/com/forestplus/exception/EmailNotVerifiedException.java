package com.forestplus.exception;

public class EmailNotVerifiedException extends ForestPlusException {
    public EmailNotVerifiedException(String email) {
        super("EMAIL_NOT_VERIFIED", 403);
    }
}
