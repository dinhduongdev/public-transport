package com.publictransport.controllers;

import com.publictransport.models.Rating;
import com.publictransport.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
public class APIRatingController {
    @Autowired
    private RatingService ratingService;

    @PostMapping
    public ResponseEntity<Rating> createRating(
            @RequestParam("userId") Long userId,
            @RequestParam("routeId") Long routeId,
            @RequestParam("score") Integer score,
            @RequestParam(value = "comment", required = false) String comment) {
        try {
            Rating rating = ratingService.createRating(userId, routeId, score, comment);
            return new ResponseEntity<>(rating, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getRatingSummary(@RequestParam("routeId") Long routeId) {
        try {
            Map<String, Object> summary = ratingService.getRatingSummary(routeId);
            return new ResponseEntity<>(summary, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}