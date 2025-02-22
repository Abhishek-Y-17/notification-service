package org.example.Exceptions;

public class InvalidPhoneNoException extends RuntimeException {
    public InvalidPhoneNoException(String message) {
        super(message);
    }
}
