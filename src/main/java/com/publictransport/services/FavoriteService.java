package com.publictransport.services;

import com.publictransport.dto.FavoriteDTO;
import com.publictransport.dto.FavoriteResolvedDTO;
import com.publictransport.models.Favorite;

import java.util.List;

public interface FavoriteService {
    Favorite saveFavorite(Favorite favorite);
    List<FavoriteDTO> getFavoritesByUserId(Long userId);
    Favorite getFavoriteByUserIdAndTarget(Long userId, Long targetId, String targetType);
    void deleteFavorite(Long id);
    List<Favorite> getObservedFavorites(Long userId);
    FavoriteDTO mapToDTO(Favorite f);
    Object resolveTarget(Favorite favorite);
    FavoriteResolvedDTO mapToResolvedDTO(Favorite f);
    List<FavoriteResolvedDTO> getResolvedFavoritesByUserId(Long userId, String targetType);
    void updateObservedStatus(Long id, boolean observed);
    List<FavoriteResolvedDTO> getResolvedFavoritesByUserIdAndType(Long userId, String type);


    List<Favorite> findObservedFavoritesByTarget(Long targetId, String targetType);
}

