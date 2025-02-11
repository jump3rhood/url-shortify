package org.john.personal.urlshortify.advices;

import lombok.extern.slf4j.Slf4j;
import org.john.personal.urlshortify.dto.response.ErrorResponse;
import org.john.personal.urlshortify.exception.UrlNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFound(UrlNotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("URL_NOT_FOUND", ex.getMessage()) {
                },
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("URL_NOT_FOUND", ex.getMessage()) {
                },
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(
            Exception ex) {

        // Log unexpected errors
        log.error("Unexpected error occurred", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        "An unexpected error occurred"
                ));
    }
}
