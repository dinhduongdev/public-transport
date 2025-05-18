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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/manage-routes")
    public String getAllRoutes(Model model,
                               @RequestParam(name = "page", defaultValue = "1") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               @RequestParam(name = "name", required = false) String name,
                               @RequestParam(name = "code", required = false) String code,
                               @RequestParam(name = "type", required = false) String type,
                               @RequestParam(name = "startStop", required = false) String startStop,
                               @RequestParam(name = "endStop", required = false) String endStop,
                               RedirectAttributes redirectAttributes) {
        try {
            if (page < 1 || size < 1) {
                redirectAttributes.addFlashAttribute("errorMsg", "Trang hoặc kích thước không hợp lệ.");
                return "redirect:/manage-routes";
            }

            // Tạo params
            Map<String, String> params = new HashMap<>();
            if (name != null && !name.trim().isEmpty()) params.put("name", name);
            if (code != null && !code.trim().isEmpty()) params.put("code", code);
            if (type != null && !type.trim().isEmpty()) params.put("type", type);
            if (startStop != null && !startStop.trim().isEmpty()) params.put("startStop", startStop);
            if (endStop != null && !endStop.trim().isEmpty()) params.put("endStop", endStop);

            List<Route> routes;
            long totalItems;
            int totalPages;

            // Nếu có tham số tìm kiếm, gọi phương thức tìm kiếm
            if (!params.isEmpty()) {
                routes = routeService.searchRoutes(params, page, size);
                totalItems = routeService.countRoutesByParams(params);
            } else {
                // Nếu không có tham số, lấy toàn bộ danh sách
                routes = routeService.findAllRoutes(page, size);
                totalItems = routeService.countAllRoutes();
            }

            // Tính tổng số trang
            totalPages = (int) Math.ceil((double) totalItems / size);

            // Kiểm tra nếu trang vượt quá tổng số trang
            if (page > totalPages && totalPages > 0) {
                redirectAttributes.addFlashAttribute("errorMsg", "Trang không tồn tại.");
                return "redirect:/manage-routes?page=" + totalPages + "&size=" + size;
            }

            // Lấy danh sách RouteVariant cho mỗi Route
            Map<Long, List<RouteVariant>> routeVariantsMap = routes.stream()
                    .collect(Collectors.toMap(
                            Route::getId,
                            route -> routeService.findRouteVariantsByRouteId(route.getId())
                    ));

            // Thêm các thuộc tính vào model
            model.addAttribute("routes", routes);
            model.addAttribute("routeVariantsMap", routeVariantsMap);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("size", size);
            model.addAttribute("name", name);
            model.addAttribute("code", code);
            model.addAttribute("type", type);
            model.addAttribute("startStop", startStop);
            model.addAttribute("endStop", endStop);

            return "route/routes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Đã xảy ra lỗi khi lấy danh sách tuyến đường: " + e.getMessage());
            return "redirect:/manage-routes";
        }
    }

    @GetMapping("/manage-routes/detail/{id}")
    public String viewRouteDetail(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Route route = routeService.findById(id);
            if (route == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "Tuyến đường không tồn tại.");
                return "redirect:/manage-routes";
            }
            List<RouteVariant> variants = routeService.findRouteVariantsByRouteId(id);
            model.addAttribute("route", route);
            model.addAttribute("variants", variants);
            return "route/route-detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Đã xảy ra lỗi khi xem chi tiết tuyến đường: " + e.getMessage());
            return "redirect:/manage-routes";
        }
    }

    @GetMapping("/manage-routes/add")
    public String showAddRouteForm(Model model) {
        model.addAttribute("route", new Route());
        return "route/route-add";
    }

    @PostMapping("/manage-routes/add")
    public String addRoute(Route route, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (route.getName() == null || route.getName().trim().isEmpty() ||
                    route.getCode() == null || route.getCode().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMsg", "Tên và mã tuyến đường không được để trống.");
                return "redirect:/manage-routes/add";
            }

            routeService.save(route);
            redirectAttributes.addFlashAttribute("successMsg", "Thêm tuyến đường thành công.");
            return "redirect:/manage-routes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Đã xảy ra lỗi khi thêm tuyến đường: " + e.getMessage());
            return "redirect:/manage-routes/add";
        }
    }

    @GetMapping("/manage-routes/edit/{id}")
    public String showEditRouteForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Route route = routeService.findById(id);
            if (route == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "Tuyến đường không tồn tại.");
                return "redirect:/manage-routes";
            }
            model.addAttribute("route", route);
            return "route/route-edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Đã xảy ra lỗi khi tải form chỉnh sửa: " + e.getMessage());
            return "redirect:/manage-routes";
        }
    }

    @PostMapping("/manage-routes/edit/{id}")
    public String updateRoute(@PathVariable("id") Long id, Route route, RedirectAttributes redirectAttributes) {
        try {
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
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Đã xảy ra lỗi khi cập nhật tuyến đường: " + e.getMessage());
            return "redirect:/manage-routes/edit/" + id;
        }
    }

    @GetMapping("/manage-routes/delete/{id}")
    public String deleteRoute(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Route route = routeService.findById(id);
            if (route == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "Tuyến đường không tồn tại.");
                return "redirect:/manage-routes";
            }
            routeService.delete(id);
            redirectAttributes.addFlashAttribute("successMsg", "Xóa tuyến đường thành công.");
            return "redirect:/manage-routes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Đã xảy ra lỗi khi xóa tuyến đường: " + e.getMessage());
            return "redirect:/manage-routes";
        }
    }
}