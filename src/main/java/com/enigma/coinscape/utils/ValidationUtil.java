package com.enigma.coinscape.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ValidationUtil {
    private final Validator validator;

    public <T> void validate(T object) {
        Set<ConstraintViolation<T>> result = validator.validate(object);
        if(!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }
    }
}