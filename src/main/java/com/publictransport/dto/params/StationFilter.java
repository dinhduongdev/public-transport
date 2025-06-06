package com.publictransport.dto.params;

import com.publictransport.models.*;
import com.publictransport.utils.Constant;
import com.publictransport.utils.MapUtils;
import com.publictransport.utils.PredicateUtils;
import com.publictransport.utils.StringUtils;
import com.publictransport.validation.ValidCoordinatesString;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.*;

import static com.publictransport.utils.MapUtils.putIfNotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class StationFilter extends BaseFilter {
    private String name;
    private String address;
    private String street;
    private String ward;
    private String zone;
    @ValidCoordinatesString
    private String latLng;
    @DecimalMax("30")
    @NonNull
    @Positive
    private Double radiusKm = Constant.DEFAULT_RADIUS;
    // Từ khóa để dùng proxy tìm tọa độ
    // nếu được cung cấp, sẽ bỏ qua hết đống còn lại trừ radius
    private String keyword;
    // Địa chỉ đã được định dạng từ proxy
    private String formattedAddress;

    public boolean isKeywordSet() {
        return StringUtils.isNotEmpty(keyword);
    }

    public Map<SingularAttribute, String> toMapOfRootStringFields() {
        Map<SingularAttribute, String> map = new HashMap<>();
        putIfNotEmpty(map, Station_.name, name);
        return map;
    }

    public Map<SingularAttribute, String> toMapOfLocationFields() {
        Map<SingularAttribute, String> params = new HashMap<>();
        putIfNotEmpty(params, Location_.address, address);
        putIfNotEmpty(params, Location_.street, street);
        putIfNotEmpty(params, Location_.ward, ward);
        putIfNotEmpty(params, Location_.zone, zone);
        return params;
    }

    @Override
    public <Station> List<Predicate> toPredicateList(CriteriaBuilder cb, Root<Station> root) {
        Map<SingularAttribute, String> rootParams = toMapOfRootStringFields();
        Map<SingularAttribute, String> locationParams = toMapOfLocationFields();
        Join<Station, Location> locationJoin = root.join(Station_.LOCATION);
        Join<Station, Coordinates> coordinatesJoin = root.join(Station_.COORDINATES);


        List<Predicate> predicates = new ArrayList<>();
        predicates.addAll(PredicateUtils.buildIContainsPredicates(rootParams, cb, root));
        predicates.addAll(PredicateUtils.buildIContainsPredicates(locationParams, cb, locationJoin));

        // Nếu tọa độ đã được thiết lập, thêm điều kiện khoảng cách
        Optional<Coordinates> optCoords = MapUtils.convertToCoordinates(latLng);
        if (optCoords.isPresent()) {
            Coordinates coords = optCoords.get();
            Expression<Double> distanceExpr = cb.function(
                    "haversine_km",
                    Double.class,
                    cb.literal(coords.getLat()), cb.literal(coords.getLng()),
                    coordinatesJoin.get(Coordinates_.lat), coordinatesJoin.get(Coordinates_.lng)
            );
            predicates.add(cb.le(distanceExpr, radiusKm));
        }

        return predicates;
    }
}
