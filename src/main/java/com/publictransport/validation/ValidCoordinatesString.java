package com.publictransport.validation;

import com.publictransport.validation.impl.ValidCoordinatesStringImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidCoordinatesStringImpl.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidCoordinatesString {
    String message() default "chuỗi tọa độ (coords) không hợp lệ. Định dạng hợp lệ: '<lat>;<lng>'.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
