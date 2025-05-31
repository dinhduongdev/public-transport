package com.publictransport.proxies;

import com.fasterxml.jackson.databind.JsonNode;
import com.publictransport.dto.DirectionRequest;
import com.publictransport.models.Coordinates;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.Set;

public interface MapProxy {
    JsonNode getDirections(DirectionRequest directionRequest);

    // Trả về: địa chỉ đề xuất, tọa độ tương ứng
    Optional<Pair<String, Coordinates>> getCoordinates(String kw);

    Optional<String> getAddress(Coordinates coordinates);
}
