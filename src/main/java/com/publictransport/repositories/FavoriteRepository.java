package com.publictransport.repositories;

import com.publictransport.models.Favorite;

import java.util.List;

public interface FavoriteRepository {
    Favorite save(Favorite favorite);
    Favorite findById(Long id);
    Favorite findByIdWithUser(Long id);
    List<Favorite> findByUserId(Long userId);
    Favorite findByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);
    List<Favorite> findAllByTargetIdAndTargetType(Long targetId, String targetType);
    List<Favorite> findByUserIdAndTargetType(Long userId, String targetType);
    void deleteById(Long id);
    List<Favorite> findByUserIdAndIsObserved(Long userId, Boolean isObserved);
    void deleteFavorite(Long id);
}
