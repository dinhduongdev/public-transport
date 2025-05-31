package com.publictransport.controllers;


import com.publictransport.dto.FavoriteDTO;
import com.publictransport.dto.FavoriteResolvedDTO;
import com.publictransport.models.Favorite;
import com.publictransport.models.User;
import com.publictransport.repositories.UserRepository;
import com.publictransport.services.FavoriteService;
import com.publictransport.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/favorites")
public class APIFavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> saveFavorite(@RequestBody Favorite favorite, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if(userId == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vui lòng đăng nhập để sử dụng tính năng này");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            Favorite savedFavorite = favoriteService.saveFavorite(favorite);
            return ResponseEntity.ok(savedFavorite);
        }catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Không thể them yêu thích");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<FavoriteResolvedDTO>> getResolvedFavorites(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "targetType", required = false) String targetType) {
        List<FavoriteResolvedDTO> favorites = favoriteService.getResolvedFavoritesByUserId(userId, targetType);
        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping("/{favoriteId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteFavorite(@PathVariable("favoriteId") Long favoriteId) {
        favoriteService.deleteFavorite(favoriteId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{favoriteId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> updateObservedStatus(@PathVariable("favoriteId") Long favoriteId,
                                                     @RequestParam("observed") boolean observed) {
        favoriteService.updateObservedStatus(favoriteId, observed);
        return ResponseEntity.noContent().build();
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
