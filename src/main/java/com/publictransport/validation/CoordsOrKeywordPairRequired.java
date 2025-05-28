package com.publictransport.validation;

import com.publictransport.validation.impl.CoordsOrKeywordPairRequiredImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = CoordsOrKeywordPairRequiredImpl.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface CoordsOrKeywordPairRequired {
    String message() default "Bạn phải cung cấp tọa độ (startCoords, endCoords) hoặc từ khóa (startKw, endKw).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
