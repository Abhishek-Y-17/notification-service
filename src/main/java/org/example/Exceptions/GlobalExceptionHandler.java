package org.example.Exceptions;

//package org.example.exception;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

// Using @RestControllerAdvice automatically applies @ResponseBody to all methods.
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<?> handleIdNotFoundException(IdNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPhoneNoException.class)
    public ResponseEntity<?> handleInvalidPhoneNoException(InvalidPhoneNoException e) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(java.io.IOException.class)
    public ResponseEntity<String> handleIOException(java.io.IOException ex) {
        log.error(ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("I/O error occurred: " + ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error(ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }
}