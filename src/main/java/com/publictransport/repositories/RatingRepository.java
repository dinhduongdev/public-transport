package com.publictransport.repositories;

import com.publictransport.models.Rating;

import java.util.List;
import java.util.Map;

public interface RatingRepository {
    Rating save(Rating rating);
    List<Rating> findByRouteId(Long routeId);
    Double getAverageScoreByRouteId(Long routeId);
    Long countByRouteId(Long routeId);
    Map<Integer, Long> countRatingsByScore(Long routeId);
    Rating findByUserIdAndRouteId(Long userId, Long routeId);
}
