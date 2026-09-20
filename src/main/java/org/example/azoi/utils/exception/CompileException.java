package org.example.azoi.utils.exception;

public class CompileException extends RuntimeException {
    public CompileException(String message) {
        super(message);
    }

    public CompileException(String message, Throwable cause) {
        super(message, cause);
    }
}