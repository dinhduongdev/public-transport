package com.publictransport.controllers;

import com.publictransport.models.Favorite;
import com.publictransport.services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manage-favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public String getFavoriteSummary(
            @RequestParam(value = "targetId", required = false) Long targetId,
            @RequestParam(value = "targetType", required = false) String targetType,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (targetId == null || targetType == null || (!targetType.equalsIgnoreCase("ROUTE") && !targetType.equalsIgnoreCase("SCHEDULE"))) {
            redirectAttributes.addFlashAttribute("errorMsg", "Vui lòng chọn một tuyến đường hoặc lịch trình hợp lệ.");
            return "redirect:/manage-routes";
        }

        try {
            List<Favorite> favorites = favoriteService.findAllFavoritesByTarget(targetId, targetType);
            if (favorites.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMsg", "Chưa có lượt yêu thích nào cho mục này.");
                return "redirect:/manage-routes";
            }

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalFavorites", favorites.size());
            summary.put("observedFavorites", favorites.stream().filter(f -> f.getIsObserved() != null && f.getIsObserved()).count());
            summary.put("favorites", favorites);

            model.addAttribute("summary", summary);
            model.addAttribute("targetId", targetId);
            model.addAttribute("targetType", targetType);
            return "favorite/favorite-summary";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy mục hoặc lỗi hệ thống.");
            return "redirect:/manage-routes";
        }
    }
}