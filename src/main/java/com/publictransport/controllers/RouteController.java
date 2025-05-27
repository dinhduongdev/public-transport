package com.publictransport.controllers;

import com.publictransport.dto.params.RouteFilter;
import com.publictransport.models.Route;
import com.publictransport.models.RouteVariant;
import com.publictransport.services.RouteService;
import com.publictransport.services.RouteVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class RouteController {

    private final RouteService routeService;
    private final RouteVariantService routeVariantService;

    @Autowired
    public RouteController(RouteService routeService, RouteVariantService routeVariantService) {
        this.routeService = routeService;
        this.routeVariantService = routeVariantService;
    }

    @GetMapping("/manage-routes")
    public String getAllRoutes(
            Model model,
            RouteFilter params,
            RedirectAttributes redirectAttributes
    ) {
        long totalRoutes = routeService.countRoutes(params);
        int totalPages = (int) Math.ceil((double) totalRoutes / params.getSize());

        // Nếu trang yêu cầu lớn hơn tổng số trang, lấy trang cuối cùng
        if (params.getPage() > totalPages) {
            redirectAttributes.addFlashAttribute("errorMsg", "Trang không tồn tại.");
            params.setPage(totalPages);
        }

        List<Route> routes = routeService.findRoutes(params, true);

        model.addAttribute("routes", routes);
        model.addAttribute("currentPage", params.getPage());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalRoutes);
        model.addAttribute("size", params.getSize());
        model.addAttribute("name", params.getName());
        model.addAttribute("code", params.getCode());
        model.addAttribute("type", params.getType());
        model.addAttribute("startStop", params.getStartStop());
        model.addAttribute("endStop", params.getEndStop());

        return "route/routes";
    }

    @GetMapping("/manage-routes/detail/{id}")
    public String viewRouteDetail(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Route> optRoute = routeService.findById(id, true);
        if (optRoute.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Tuyến đường không tồn tại.");
            return "redirect:/manage-routes";
        }
        // Lấy danh sách các RouteVariant liên quan đến tuyến đường này
        Route route = optRoute.get();
        Set<RouteVariant> routeVariants = route.getRouteVariants();

        model.addAttribute("route", route);
        model.addAttribute("variants", routeVariants);
        return "route/route-detail";
    }

    @GetMapping("/manage-routes/add")
    public String showAddRouteForm(Model model) {
        model.addAttribute("route", new Route());
        return "route/route-add";
    }

    @PostMapping("/manage-routes/add")
    public String addRoute(Route route, RedirectAttributes redirectAttributes) {
        // Kiểm tra dữ liệu đầu vào
        if (route.getName() == null || route.getName().trim().isEmpty() ||
                route.getCode() == null || route.getCode().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Tên và mã tuyến đường không được để trống.");
            return "redirect:/manage-routes/add";
        }

        routeService.save(route);
        redirectAttributes.addFlashAttribute("successMsg", "Thêm tuyến đường thành công.");
        return "redirect:/manage-routes";
    }

    @GetMapping("/manage-routes/edit/{id}")
    public String showEditRouteForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Route> route = routeService.findById(id);
        if (route.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Tuyến đường không tồn tại.");
            return "redirect:/manage-routes";
        }
        // Lấy danh sách các RouteVariant liên quan đến tuyến đường này
        List<RouteVariant> routeVariants = routeVariantService.findByRouteId(id);
        model.addAttribute("route", route.get());
        model.addAttribute("variants", routeVariants);
        return "route/route-edit";
    }

    @PostMapping("/manage-routes/edit/{id}")
    public String updateRoute(@PathVariable("id") Long id, Route route, RedirectAttributes redirectAttributes) {
        // Kiểm tra dữ liệu đầu vào
        if (route.getName() == null || route.getName().trim().isEmpty() ||
                route.getCode() == null || route.getCode().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Tên và mã tuyến đường không được để trống.");
            return "redirect:/manage-routes/edit/" + id;
        }

        route.setId(id);
        routeService.update(route);
        redirectAttributes.addFlashAttribute("successMsg", "Cập nhật tuyến đường thành công.");
        return "redirect:/manage-routes";
    }

    @GetMapping("/manage-routes/delete/{id}")
    public String deleteRoute(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Route> route = routeService.findById(id);
        if (route.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Tuyến đường không tồn tại.");
            return "redirect:/manage-routes";
        }
        routeService.delete(id);
        redirectAttributes.addFlashAttribute("successMsg", "Xóa tuyến đường thành công.");
        return "redirect:/manage-routes";
    }
}