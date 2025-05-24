package com.publictransport.proxies;

import com.fasterxml.jackson.databind.JsonNode;
import com.publictransport.dto.DirectionRequest;

public interface MapProxy {
    JsonNode getDirections(DirectionRequest directionRequest);
}
