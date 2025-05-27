package com.publictransport.dto.params;

import com.publictransport.utils.Constant;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public abstract class BaseFilter {
    @Positive
    private int page = Constant.DEFAULT_PAGE;
    @Positive
    private int size = Constant.DEFAULT_SIZE;

    public abstract <T> List<Predicate> toPredicateList(CriteriaBuilder cb, Root<T> root);
}
