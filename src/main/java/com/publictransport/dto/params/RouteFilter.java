package com.publictransport.dto.params;

import com.publictransport.models.Route;
import com.publictransport.models.RouteVariant;
import com.publictransport.models.RouteVariant_;
import com.publictransport.models.Route_;
import com.publictransport.utils.PredicateUtils;
import com.publictransport.utils.StringUtils;
import com.publictransport.validation.StopKeywordPair;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.publictransport.utils.MapUtils.putIfNotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@StopKeywordPair
public class RouteFilter extends BaseFilter {
    private String name;
    private String code;
    private String type;
    private String startStop;
    private String endStop;

    // các trường có kw sẽ dùng proxy để tìm
    // nếu được cung cấp sẽ không dùng đống ở trên
    // đống ở trên là các trường trong model
    private String startKw;
    private String endKw;

    public boolean areKeywordsSet() {
        return StringUtils.allOrNoneNotEmpty(startKw, endKw);
    }

    public Map<SingularAttribute, String> toMapOfRootStringFields() {
        Map<SingularAttribute, String> params = new HashMap<>();
        putIfNotEmpty(params, Route_.name, name);
        putIfNotEmpty(params, Route_.code, code);
        putIfNotEmpty(params, Route_.type, type);
        return params;
    }

    public Map<SingularAttribute, String> toMapOfJoinedStringFields() {
        Map<SingularAttribute, String> params = new HashMap<>();
        putIfNotEmpty(params, RouteVariant_.startStop, startStop);
        putIfNotEmpty(params, RouteVariant_.endStop, endStop);
        return params;
    }

    @Override
    public <Route> List<Predicate> toPredicateList(CriteriaBuilder cb, Root<Route> root) {
        Map<SingularAttribute, String> rootParams = this.toMapOfRootStringFields();
        Join<Route, RouteVariant> joinRoot = root.join("routeVariants", JoinType.INNER);
        Map<SingularAttribute, String> routeVarParams = this.toMapOfJoinedStringFields();

        List<Predicate> preds = new ArrayList<>();
        preds.addAll(PredicateUtils.buildIContainsPredicates(rootParams, cb, root));
        preds.addAll(PredicateUtils.buildIContainsPredicates(routeVarParams, cb, joinRoot));
        return preds;
    }
}
