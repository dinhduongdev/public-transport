package com.publictransport.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

//https://www.baeldung.com/spring-mvc-custom-validator - 9

@Documented
@Constraint(validatedBy = CoordinatePairValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface CoordinatePair {
    String message() default "Kinh độ (lng) & Vĩ độ (lat) phải được cung cấp cùng nhau.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

