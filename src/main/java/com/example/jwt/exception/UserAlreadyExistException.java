package com.example.jwt.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
