package com.publictransport.services;

import com.publictransport.models.Rating;

import java.util.Map;

public interface RatingService {
    Rating createRating(Long userId, Long routeId, Integer score, String comment);
    Map<String, Object> getRatingSummary(Long routeId);
    Rating findById(Long id);
    void delete(Long id);
}
