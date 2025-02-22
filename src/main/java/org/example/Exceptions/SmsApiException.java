package org.example.Exceptions;

public class SmsApiException extends RuntimeException {
    private final String errorCode;

    public SmsApiException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SmsApiException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
