package org.john.personal.urlshortify.exception;


public class UrlNotFoundException extends RuntimeException{
    public UrlNotFoundException(String message) {
        super(message);
    }
}
