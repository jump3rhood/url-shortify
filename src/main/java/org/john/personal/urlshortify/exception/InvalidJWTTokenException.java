package org.john.personal.urlshortify.exception;

public class InvalidJWTTokenException extends RuntimeException{
    public InvalidJWTTokenException(String message) {
        super(message);
    }
}
