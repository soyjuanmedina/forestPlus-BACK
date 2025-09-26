package com.forestplus.exception;

public class WrongPasswordException extends ForestPlusException {
    public WrongPasswordException() {
        super("WRONG_PASSWORD", 403);
    }
}
