package org.example.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    public ErrorResponse(LocalDateTime timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;

    }

}
