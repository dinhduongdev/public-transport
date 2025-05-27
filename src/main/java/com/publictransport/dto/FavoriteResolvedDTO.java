package com.publictransport.dto;

import lombok.Data;

@Data
public class FavoriteResolvedDTO {
    private Long id;
    private Boolean isObserved;
    private String targetType;
    private Long targetId;
    private Object targetData;
}
