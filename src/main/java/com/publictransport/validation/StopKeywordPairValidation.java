package com.publictransport.validation;

import com.publictransport.dto.params.RouteFilter;
import com.publictransport.utils.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StopKeywordPairValidation implements
        ConstraintValidator<StopKeywordPair, RouteFilter> {
    @Override
    public boolean isValid(RouteFilter filter, ConstraintValidatorContext context) {
        if (filter == null) {
            return true;
        }
        return StringUtils.allOrNoneNotEmpty(filter.getStartKw(), filter.getEndKw());
    }
}
