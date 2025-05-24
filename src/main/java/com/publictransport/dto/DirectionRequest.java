package com.publictransport.dto;

import lombok.Data;

@Data
public class DirectionRequest {
    private String origin;
    private String destination;
    private String apiKey;
    private String vehicle = "car";
}
