package com.publictransport.validation.impl;

import com.publictransport.dto.params.RouteNavigationFilter;
import com.publictransport.utils.StringUtils;
import com.publictransport.validation.CoordsOrKeywordPairRequired;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CoordsOrKeywordPairRequiredImpl implements
        ConstraintValidator<CoordsOrKeywordPairRequired, RouteNavigationFilter> {

    @Override
    public boolean isValid(RouteNavigationFilter filter, ConstraintValidatorContext context) {
        boolean areCoordsProvided = StringUtils.isNotEmpty(filter.getStartCoords(), filter.getEndCoords());
        boolean areKeywordsProvided = StringUtils.isNotEmpty(filter.getStartKw(), filter.getEndKw());
        return areCoordsProvided || areKeywordsProvided;
    }
}
