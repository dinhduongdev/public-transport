package com.publictransport.services.impl;

import com.publictransport.models.Rating;
import com.publictransport.models.Route;
import com.publictransport.models.User;
import com.publictransport.repositories.RatingRepository;
import com.publictransport.services.RatingService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RatingServiceImpl implements RatingService {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public Rating createRating(Long userId, Long routeId, Integer score, String comment) {
        Session s = this.factory.getObject().getCurrentSession();

        User user = s.get(User.class, userId);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        Route route = s.get(Route.class, routeId);
        if (route == null) {
            throw new RuntimeException("Không tìm thấy tuyến đường");
        }

        // Kiểm tra xem người dùng đã đánh giá chưa
        Rating existingRating = ratingRepository.findByUserIdAndRouteId(userId, routeId);

        if (existingRating != null) {
            existingRating.setScore(score);
            existingRating.setComment(comment);
            return ratingRepository.save(existingRating);
        } else {
            Rating rating = new Rating();
            rating.setUser(user);
            rating.setRoute(route);
            rating.setScore(score);
            rating.setComment(comment);
            return ratingRepository.save(rating);
        }
    }

    @Override
    public Map<String, Object> getRatingSummary(Long routeId) {
        Map<String, Object> summary = new HashMap<>();

        Double averageScore = ratingRepository.getAverageScoreByRouteId(routeId);
        summary.put("averageScore", averageScore != null ? Math.round(averageScore * 100.0) / 100.0 : 0.0);

        Long totalRatings = ratingRepository.countByRouteId(routeId);
        summary.put("totalRatings", totalRatings);

        Map<Integer, Long> ratingDistribution = ratingRepository.countRatingsByScore(routeId);
        summary.put("ratingDistribution", ratingDistribution);

        List<Rating> ratings = ratingRepository.findByRouteId(routeId);
        summary.put("ratings", ratings);

        return summary;
    }
}