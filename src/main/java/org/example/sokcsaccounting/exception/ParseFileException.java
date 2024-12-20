package org.example.sokcsaccounting.exception;

public class ParseFileException extends RuntimeException {
    public ParseFileException(Exception exception) {
        super(exception);
    }
}
