package id.co.awan.tap2pay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        // Customize your error response here
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleRcException(ResponseStatusException ex) {

        // Customize your error response here
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("error", ex.getReason() != null ? ex.getReason() : "General Error"));
    }

}
