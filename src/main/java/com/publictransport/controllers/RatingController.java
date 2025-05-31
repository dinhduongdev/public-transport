package com.publictransport.controllers;

import com.publictransport.models.Rating;
import com.publictransport.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/manage-ratings")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public String getRatingSummary(
            @RequestParam(value = "routeId", required = false) Long routeId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (routeId == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Vui lòng chọn một tuyến đường.");
            return "redirect:/manage-routes";
        }

        try {
            Map<String, Object> summary = ratingService.getRatingSummary(routeId);
            if ((Long) summary.get("totalRatings") == 0) {
                redirectAttributes.addFlashAttribute("errorMsg", "Chưa có đánh giá nào cho tuyến đường này.");
                return "redirect:/manage-routes";
            }

            model.addAttribute("summary", summary);
            model.addAttribute("routeId", routeId);
            return "rating/rating-summary";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy tuyến đường hoặc lỗi hệ thống.");
            return "redirect:/manage-routes";
        }
    }

    @GetMapping("/detail/{id}")
    public String viewRatingDetail(
            @PathVariable("id") Long id,
            @RequestParam("routeId") Long routeId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Rating rating = ratingService.findById(id);
        if (rating == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Đánh giá không tồn tại.");
            return "redirect:/manage-ratings?routeId=" + routeId;
        }

        model.addAttribute("rating", rating);
        model.addAttribute("routeId", routeId);
        return "rating/rating-detail";
    }

    @GetMapping("/delete/{id}")
    public String deleteRating(
            @PathVariable("id") Long id,
            @RequestParam("routeId") Long routeId,
            RedirectAttributes redirectAttributes
    ) {
        Rating rating = ratingService.findById(id);
        if (rating == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Đánh giá không tồn tại.");
            return "redirect:/manage-ratings?routeId=" + routeId;
        }

        ratingService.delete(id);
        redirectAttributes.addFlashAttribute("successMsg", "Xóa đánh giá thành công.");
        return "redirect:/manage-ratings?routeId=" + routeId;
    }
}