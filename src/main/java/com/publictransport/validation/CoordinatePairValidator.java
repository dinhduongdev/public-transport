package com.publictransport.validation;

import com.publictransport.dto.params.StationFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CoordinatePairValidator implements
        ConstraintValidator<CoordinatePair, StationFilter> {
    @Override
    public boolean isValid(StationFilter value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        boolean lngProvided = value.getLng() != null;
        boolean latProvided = value.getLat() != null;
        // Valid if both are null or both are non-null
        return lngProvided == latProvided;
    }
}

