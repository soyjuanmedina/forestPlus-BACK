package com.forestplus.exception;

public class EmailSendException extends ForestPlusException {
    public EmailSendException(String email) {
        super("EMAIL_SEND_FAILED", 500);
    }
}
