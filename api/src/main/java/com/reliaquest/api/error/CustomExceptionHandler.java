package com.reliaquest.api.error;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Collectors;
import lombok.Getter;
import org.slf4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class CustomExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @Getter
    public static class ErrorResponse {
        private final Instant timestamp;
        private final int status;
        private final String error;
        private final String message;
        private final String path;

        public ErrorResponse(int status, String error, String message, String path) {
            this.timestamp = Instant.now();
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }
    }
    /*
     * This is to map MethodArgumentNotValidException (e.g. for downstream validation) to 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation failed: {}", fieldErrors);
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        ErrorResponse body =
                new ErrorResponse(status.value(), status.getReasonPhrase(), fieldErrors, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
    /*
     * This is to map IllegalArgumentException (e.g. for downstream validation) to 400 as well
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse body =
                new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
    /*
     * This is custom exception to handle too many request
     */
    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequests(
            HttpClientErrorException.TooManyRequests ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS; // 429
        String message = ex.getResponseBodyAsString();
        if (message == null || message.isBlank()) {
            message = "Too many requests, please try again later.";
        }
        log.warn("429 Too Many Requests: {}", message);
        ErrorResponse body =
                new ErrorResponse(status.value(), status.getReasonPhrase(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
