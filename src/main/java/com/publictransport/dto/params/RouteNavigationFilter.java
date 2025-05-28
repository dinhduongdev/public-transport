package com.publictransport.dto.params;

import com.publictransport.utils.StringUtils;
import com.publictransport.validation.CoordsOrKeywordPairRequired;
import com.publictransport.validation.ValidCoordinatesString;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@CoordsOrKeywordPairRequired
public class RouteNavigationFilter extends BaseFilter {
    // 2 trường này có định dạng: <lng>;<lat>
    @ValidCoordinatesString
    private String startCoords;
    @ValidCoordinatesString
    private String endCoords;
    // các trường có kw sẽ dùng proxy để tìm
    // nếu được cung cấp sẽ dùng để build một filter mới với lng lat trên
    private String startKw;
    private String endKw;
    // Số lượng chuyến tối đa
    @Positive
    @Max(3)
    private int maxNumOfTrip = 1;

    public boolean areCoordsSet() {
        return StringUtils.isNotEmpty(startCoords, endCoords);
    }

    public boolean areKeywordsSet() {
        return StringUtils.isNotEmpty(startKw, endKw);
    }

    @Override
    public <T> List<Predicate> toPredicateList(CriteriaBuilder cb, Root<T> root) {
        return List.of();
    }
}
