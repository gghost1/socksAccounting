package org.example.sokcsaccounting.config;

import lombok.extern.slf4j.Slf4j;
import org.example.sokcsaccounting.exception.IllegalOperationException;
import org.example.sokcsaccounting.exception.ParseFileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalOperationException.class})
    public ResponseEntity<?> handleIllegalArgumentException(RuntimeException e) {
        log.error("{}", e.getMessage());
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(ParseFileException.class)
    public ResponseEntity<?> handleParseFileException(ParseFileException e) {
        log.error("{}", e.getMessage());
        return ResponseEntity.status(500).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("{}", ex.getMessage());
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .toList();
        return ResponseEntity.status(400).body(Map.of("errors", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleParseError(HttpMessageNotReadableException ex) {
        log.error("{}", ex.getMessage());
        return ResponseEntity
                .status(400)
                .body(Map.of(
                        "error", "Malformed JSON request",
                        "message", Objects.requireNonNull(ex.getMessage())
                        )
                );
    }

}
