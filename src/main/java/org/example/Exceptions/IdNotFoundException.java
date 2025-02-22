package org.example.Exceptions;

public class IdNotFoundException extends RuntimeException {

    public IdNotFoundException(String message) {
        super(message);
    }
}