package com.publictransport.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = StopKeywordPairValidation.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface StopKeywordPair {
    String message() default "cả keyword điểm đến và điểm đi phải được cung cấp cùng nhau.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
