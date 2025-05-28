package com.publictransport.validation.impl;

import com.publictransport.models.Coordinates;
import com.publictransport.utils.MapUtils;
import com.publictransport.validation.ValidCoordinatesString;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class ValidCoordinatesStringImpl implements
        ConstraintValidator<ValidCoordinatesString, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        Optional<Coordinates> optCoords = MapUtils.convertToCoordinates(s);
        return optCoords.isPresent();
    }
}
