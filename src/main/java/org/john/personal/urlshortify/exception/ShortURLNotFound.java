package org.john.personal.urlshortify.exception;


public class ShortURLNotFound extends RuntimeException{
    public ShortURLNotFound(String message) {
        super(message);
    }
}
