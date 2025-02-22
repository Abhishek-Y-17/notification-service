package org.example.Exceptions;

public class PhoneNoNotFoundException extends RuntimeException {
    public PhoneNoNotFoundException(String message) {
        super(message);
    }
}
