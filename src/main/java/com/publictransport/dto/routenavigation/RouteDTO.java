package com.publictransport.dto.routenavigation;

import com.publictransport.models.Route;
import com.publictransport.models.RouteType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteDTO {
    private Long id;

    private String code;

    private String name;

    private RouteType type;

    public RouteDTO(Route route) {
        this.id = route.getId();
        this.code = route.getCode();
        this.name = route.getName();
        this.type = route.getType();
    }
}
