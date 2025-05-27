package com.publictransport.dto;

import lombok.Data;

@Data
public class FavoriteDTO {
    private Long id;
    private Long userId;
    private Long targetId;
    private String targetType;
    private Boolean isObserved;
}
