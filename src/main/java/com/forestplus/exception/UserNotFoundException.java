package com.forestplus.exception;

public class UserNotFoundException extends ForestPlusException {
    public UserNotFoundException(String email) {
        super("USER_NOT_FOUND: " + email, 404);
    }
}
