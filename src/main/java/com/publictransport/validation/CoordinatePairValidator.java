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
        // cần phải cung cấp cả 2 hoặc không cung cấp cái nào
        return lngProvided == latProvided;
    }
}

