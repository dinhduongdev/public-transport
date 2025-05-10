package com.publictransport.controllers;

import com.publictransport.models.Route;
import com.publictransport.models.RouteVariant;
import com.publictransport.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/manage-routes")
    public String getAllRoutes(Model model,
                               @RequestParam(name = "page", defaultValue = "1") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               @RequestParam(name = "keyword", required = false) String keyword) {
        List<Route> routes;
        int totalPages;

        // Tạo params để truyền vào phương thức tìm kiếm
        Map<String, String> params = new HashMap<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword);
        }

        // Nếu có tham số tìm kiếm, gọi phương thức tìm kiếm
        if (!params.isEmpty()) {
            routes = routeService.searchRoutes(params, page, size);
            totalPages = routeService.getTotalPagesByParams(params, size);
        } else {
            // Nếu không có tham số, lấy toàn bộ danh sách
            routes = routeService.getAllRoutes(page, size);
            totalPages = routeService.getTotalPages(size);
        }

        // Lấy danh sách RouteVariant cho mỗi Route
        Map<Long, List<RouteVariant>> routeVariantsMap = new HashMap<>();
        for (Route route : routes) {
            List<RouteVariant> variants = routeService.getRouteVariantsByRouteId(route.getId());
            System.out.println("Route: " + route.getName() + " (ID: " + route.getId() + ")");
            for (RouteVariant variant : variants) {
                System.out.println("  Variant ID: " + variant.getId() + ", Tên: " + variant.getName());
            }
            routeVariantsMap.put(route.getId(), variants);
        }

        model.addAttribute("routes", routes);
        model.addAttribute("routeVariantsMap", routeVariantsMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword); // Truyền keyword để giữ giá trị trong form
        return "routes";
    }

    @GetMapping("/manage-routes/detail/{id}")
    public String viewRouteDetail(@PathVariable("id") Long id, Model model) {
        Route route = routeService.getRouteById(id);
        if (route == null) {
            model.addAttribute("msg", "Tuyến đường không tồn tại.");
            return "redirect:/manage-routes";
        }
        List<RouteVariant> variants = routeService.getRouteVariantsByRouteId(id);
        model.addAttribute("route", route);
        model.addAttribute("variants", variants);
        return "route-detail";
    }

    @GetMapping("/manage-routes/add")
    public String showAddRouteForm(Model model) {
        model.addAttribute("route", new Route());
        return "route-add";
    }

    @PostMapping("/manage-routes/add")
    public String addRoute(Route route) {
        routeService.saveRoute(route);
        return "redirect:/manage-routes";
    }

    @GetMapping("/manage-routes/edit/{id}")
    public String showEditRouteForm(@PathVariable("id") Long id, Model model) {
        Route route = routeService.getRouteById(id);
        if (route == null) {
            model.addAttribute("msg", "Tuyến đường không tồn tại.");
            return "redirect:/manage-routes";
        }
        model.addAttribute("route", route);
        return "route-edit";
    }

    @PostMapping("/manage-routes/edit/{id}")
    public String updateRoute(@PathVariable("id") Long id, Route route) {
        route.setId(id);
        routeService.updateRoute(route);
        return "redirect:/manage-routes";
    }

    @GetMapping("/manage-routes/delete/{id}")
    public String deleteRoute(@PathVariable("id") Long id) {
        routeService.deleteRoute(id);
        return "redirect:/manage-routes";
    }
}