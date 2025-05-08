package com.mylearning.poc.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageUrlValidator implements ConstraintValidator<ValidImageUrl, String> {

    private static final String IMAGE_URL_PATTERN =
            ".*\\.(jpg|jpeg|png|gif|bmp|webp)$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return value.toLowerCase().matches(IMAGE_URL_PATTERN);
    }
}