package com.publictransport.controllers;


import com.publictransport.dto.FavoriteDTO;
import com.publictransport.dto.FavoriteResolvedDTO;
import com.publictransport.models.Favorite;
import com.publictransport.services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class APIFavoriteController {
    @Autowired
    private FavoriteService favoriteService;


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Favorite> saveFavorite(@RequestBody Favorite favorite) {
        Favorite savedFavorite = favoriteService.saveFavorite(favorite);
        return ResponseEntity.ok(savedFavorite);
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

}
