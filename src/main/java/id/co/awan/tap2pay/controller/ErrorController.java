package id.co.awan.tap2pay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Class untuk handle semua Error yang terjadi di level Controller
 */
@RestControllerAdvice
public class ErrorController {

    /**
     * Handle default exception level tertinggi, yaitu {@link  Throwable}
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        // Customize your error response here
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handle exception yang sudah terdefinisi HTTP
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleRcException(ResponseStatusException ex) {

        // Customize your error response here
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("error", ex.getReason() != null ? ex.getReason() : "General Error"));
    }

}
