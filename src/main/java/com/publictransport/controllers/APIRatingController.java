package com.publictransport.controllers;

import com.publictransport.models.Rating;
import com.publictransport.models.User;
import com.publictransport.repositories.UserRepository;
import com.publictransport.services.RatingService;
import com.publictransport.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
public class APIRatingController {
    @Autowired
    private RatingService ratingService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createRating(
            @RequestParam("userId") Long userId,
            @RequestParam("routeId") Long routeId,
            @RequestParam("score") Integer score,
            @RequestParam(value = "comment", required = false) String comment,
            HttpServletRequest request) {
        try {
            Long userIdFromToken = getUserIdFromRequest(request);
            if (userIdFromToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Vui lòng đăng nhập để sử dụng tính năng này"));
            }
            if (!userIdFromToken.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Không thể đánh giá với tài khoản người khác"));
            }

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











    private Long getUserIdFromRequest(HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                String email = jwtUtils.validateTokenAndGetEmail(token);

                if (email != null) {
                    Optional<User> userOpt = userRepository.getUserByEmail(email);
                    if (userOpt.isPresent()) {
                        return userOpt.get().getId();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}