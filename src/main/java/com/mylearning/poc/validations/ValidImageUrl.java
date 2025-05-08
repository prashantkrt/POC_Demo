package com.mylearning.poc.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageUrlValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageUrl {
    String message() default "Invalid image URL. Allowed formats: .jpg, .jpeg, .png, .gif, .bmp, .webp";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}