package com.forestplus.exception;

import org.springframework.http.HttpStatus;

public class UuidNotFoundException extends ForestPlusException {
    public UuidNotFoundException(String uuid) {
        super(HttpStatus.NOT_FOUND, "UUID_NOT_FOUND");
    }
}
