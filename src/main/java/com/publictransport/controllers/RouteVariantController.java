package com.publictransport.controllers;

import com.publictransport.models.RouteVariant;
import com.publictransport.services.RouteVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RouteVariantController {

    @Autowired
    private RouteVariantService routeVariantService;

    @GetMapping("/manage-route-variants")
    public String listRouteVariants(Model model,
                                    @RequestParam(name = "page", defaultValue = "1") int page,
                                    @RequestParam(name = "size", defaultValue = "10") int size) {

        List<RouteVariant> routeVariants = routeVariantService.getRouteVariants(page, size);
        long totalItems = routeVariantService.countRouteVariants();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        model.addAttribute("routeVariants", routeVariants);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);

        return "route-variants";
    }
}
