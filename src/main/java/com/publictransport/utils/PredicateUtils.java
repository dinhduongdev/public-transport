package com.publictransport.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.SingularAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PredicateUtils {
    private PredicateUtils() {
    }


    // Hàm này để build predicates cho các trường String trong entity
    // Hàm so sánh kiểu contains và ignore case
    // Hỗ trợ cả root và join (root và join đều kế thừa từ From<?, ?>)
    public static List<Predicate> buildIContainsPredicates(
            Map<SingularAttribute, String> params,
            CriteriaBuilder criteriaBuilder,
            From<?, ?> rootOrJoinRoot
    ) {
        List<Predicate> predicates = new ArrayList<>();
        if (!params.isEmpty()) {
            params.forEach((field_name, value) -> {
                Expression<String> lookupFieldExpr = criteriaBuilder.lower(rootOrJoinRoot.get(field_name));
                String lookupVal = "%" + value.trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(lookupFieldExpr, lookupVal));
            });
        }
        return predicates;
    }

}
