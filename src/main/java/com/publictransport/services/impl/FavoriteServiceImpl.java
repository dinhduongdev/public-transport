package com.publictransport.services.impl;

import com.publictransport.dto.FavoriteDTO;
import com.publictransport.dto.FavoriteResolvedDTO;
import com.publictransport.models.Favorite;
import com.publictransport.models.User;
import com.publictransport.repositories.FavoriteRepository;
import com.publictransport.repositories.RouteRepository;
import com.publictransport.repositories.ScheduleRepository;
import com.publictransport.repositories.UserRepository;
import com.publictransport.services.FavoriteService;
import com.publictransport.utils.SecurityUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;



    @Override
    public Favorite saveFavorite(Favorite favorite) {
        return favoriteRepository.save(favorite);
    }

    @Override
    public List<FavoriteDTO> getFavoritesByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId)
                .stream().map(this::mapToDTO).toList();
    }

    @Override
    public Favorite getFavoriteByUserIdAndTarget(Long userId, Long targetId, String targetType) {
        return favoriteRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);
    }

    @Override
    public void deleteFavorite(Long id) {
        Favorite favorite = favoriteRepository.findByIdWithUser(id);
        if (favorite == null)
            throw new RuntimeException("Favorite not found");
        String currentEmail = SecurityUtils.getCurrentUserEmail();
        if (!favorite.getUser().getEmail().equals(currentEmail))
            throw new AccessDeniedException("You are not the owner of this favorite");
        favoriteRepository.deleteFavorite(id);
    }

    @Override
    public List<Favorite> getObservedFavorites(Long userId) {
        return favoriteRepository.findByUserIdAndIsObserved(userId, true);
    }


    @Override
    public FavoriteDTO mapToDTO(Favorite f) {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setId(f.getId());
        dto.setUserId(f.getUser().getId());
        dto.setTargetId(f.getTargetId());
        dto.setTargetType(f.getTargetType());
        dto.setIsObserved(f.getIsObserved());
        return dto;
    }

//    @Override
//    public Object resolveTarget(Favorite favorite) {
//        if ("ROUTE".equalsIgnoreCase(favorite.getTargetType())) {
//            return routeRepository.findById(favorite.getTargetId());
//        } else if ("SCHEDULE".equalsIgnoreCase(favorite.getTargetType())) {
//            return scheduleRepository.findById(favorite.getTargetId());
//        } else {
//            throw new IllegalArgumentException("Unknown targetType: " + favorite.getTargetType());
//        }
//    }
    @Override
    public Object resolveTarget(Favorite favorite) {
        if ("ROUTE".equalsIgnoreCase(favorite.getTargetType())) {
            return routeRepository.findByIdWithRouteVariants(favorite.getTargetId())
                    .orElse(null);
        } else if ("SCHEDULE".equalsIgnoreCase(favorite.getTargetType())) {
            return scheduleRepository.findById(favorite.getTargetId());
        } else {
            throw new IllegalArgumentException("Unknown targetType: " + favorite.getTargetType());
        }
    }

    @Override
    public FavoriteResolvedDTO mapToResolvedDTO(Favorite f) {
        FavoriteResolvedDTO dto = new FavoriteResolvedDTO();
        dto.setId(f.getId());
        dto.setIsObserved(f.getIsObserved());
        dto.setTargetId(f.getTargetId());
        dto.setTargetType(f.getTargetType());
        dto.setTargetData(resolveTarget(f));
        return dto;
    }

    @Override
    public List<FavoriteResolvedDTO> getResolvedFavoritesByUserId(Long userId, String targetType) {
//        List<Favorite> list = favoriteRepository.findByUserId(userId);
//        return list.stream().map(this::mapToResolvedDTO).toList();
        List<Favorite> favorites;
        if (targetType != null && !targetType.isEmpty()) {
            favorites = favoriteRepository.findByUserIdAndTargetType(userId, targetType);
        } else {
            favorites = favoriteRepository.findByUserId(userId);
        }
        return favorites.stream()
                .map(this::mapToResolvedDTO)
                .toList();
    }

    @Override
    public void updateObservedStatus(Long id, boolean observed) {
        Favorite favorite = favoriteRepository.findByIdWithUser(id);
        if (favorite == null)
            throw new RuntimeException("Favorite not found");

        String currentEmail = SecurityUtils.getCurrentUserEmail();
        System.out.println("Current email: " + currentEmail);
        if (!favorite.getUser().getEmail().equals(currentEmail))
            throw new AccessDeniedException("You are not the owner of this favorite");
        favorite.setIsObserved(observed);
        favoriteRepository.save(favorite);
    }

    @Override
    public List<FavoriteResolvedDTO> getResolvedFavoritesByUserIdAndType(Long userId, String type) {
        List<Favorite> favorites = favoriteRepository.findByUserIdAndTargetType(userId, type);
        return favorites.stream()
                .map(this::mapToResolvedDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Favorite> findObservedFavoritesByTarget(Long targetId, String targetType) {
        return favoriteRepository.findAllByTargetIdAndTargetType(targetId, targetType)
                .stream()
                .filter(Favorite::getIsObserved)
                .collect(Collectors.toList());
    }

}
