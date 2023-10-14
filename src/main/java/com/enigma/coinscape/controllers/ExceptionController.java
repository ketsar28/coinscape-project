package com.enigma.coinscape.controllers;

import com.enigma.coinscape.models.responses.general.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<String>> constraintViolationException(ConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.<String>builder().message(exception.getMessage())
                        .statusCode(HttpStatus.BAD_REQUEST.value()).build());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CommonResponse<String>> responseApiException(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(CommonResponse.<String>builder().message(exception.getReason())
                        .statusCode(exception.getStatus().value()).build());
    }
}
