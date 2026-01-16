package com.forestplus.exception;

import org.springframework.http.HttpStatus;

public class ForestPlusException extends RuntimeException {
    private final int status;

    public ForestPlusException(String message, int status) {
        super(message);
        this.status = status;
    }

    public ForestPlusException(HttpStatus httpStatus, String message) {
        super(message);
        this.status = httpStatus.value();
    }

	public int getStatus() {
        return status;
    }
}
