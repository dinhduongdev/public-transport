//package com.publictransport.controllers;
//
//import com.publictransport.models.RouteVariant;
//import com.publictransport.models.Stop;
//import com.publictransport.services.RouteVariantService;
//import com.publictransport.services.StopService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Controller
//@RequestMapping("/manage-route-variants")
//public class RouteVariantController {
//    @Autowired
//    private StopService stopService;
//    @Autowired
//    private RouteVariantService routeVariantService;
//
//    @GetMapping
//    public String getAllRouteVariants(Model model,
//                                      @RequestParam(name = "page", defaultValue = "1") int page,
//                                      @RequestParam(name = "size", defaultValue = "10") int size,
//                                      @RequestParam(name = "name", required = false) String name,
//                                      @RequestParam(name = "startStop", required = false) String startStop,
//                                      @RequestParam(name = "endStop", required = false) String endStop) {
//        List<RouteVariant> routeVariants;
//        long totalItems;
//        int totalPages;
//
//        // Tạo params để truyền vào phương thức tìm kiếm
//        Map<String, String> params = new HashMap<>();
//        if (name != null && !name.trim().isEmpty()) {
//            params.put("name", name);
//        }
//        if (startStop != null && !startStop.trim().isEmpty()) {
//            params.put("startStop", startStop);
//        }
//        if (endStop != null && !endStop.trim().isEmpty()) {
//            params.put("endStop", endStop);
//        }
//
//        // Nếu có tham số tìm kiếm, gọi phương thức tìm kiếm
//        if (!params.isEmpty()) {
//            routeVariants = routeVariantService.searchRouteVariants(params, page, size);
//            totalItems = routeVariantService.countRouteVariantsByParams(params);
//            totalPages = (int) Math.ceil((double) totalItems / size);
//        } else {
//            // Nếu không có tham số, lấy toàn bộ danh sách
//            routeVariants = routeVariantService.findAllRouteVariants(page, size);
//            totalItems = routeVariantService.countAllRouteVariants();
//            totalPages = (int) Math.ceil((double) totalItems / size);
//        }
//
//        model.addAttribute("routeVariants", routeVariants);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", totalPages);
//        model.addAttribute("totalItems", totalItems);
//        model.addAttribute("size", size);
//        model.addAttribute("name", name);
//        model.addAttribute("startStop", startStop);
//        model.addAttribute("endStop", endStop);
//        return "route-variant/route-variants";
//    }
//
//    @GetMapping("/detail/{id}")
//    public String viewRouteVariantDetail(@PathVariable("id") Long id, Model model) {
//        RouteVariant routeVariant = routeVariantService.findById(id);
//        if (routeVariant == null) {
//            model.addAttribute("msg", "Lộ trình không tồn tại.");
//            return "redirect:/manage-route-variants";
//        }
//        // Lấy danh sách Stop liên quan đến RouteVariant
//        List<Stop> stops = stopService.findStopsByRouteVariantId(id);
//        model.addAttribute("routeVariant", routeVariant);
//        model.addAttribute("stops", stops);
//        return "route-variant/route-variant-detail";
//    }
//
//    @GetMapping("/add")
//    public String showAddRouteVariantForm(Model model) {
//        model.addAttribute("routeVariant", new RouteVariant());
//        return "route-variant/route-variant-add";
//    }
//
//    @PostMapping("/add")
//    public String addRouteVariant(RouteVariant routeVariant) {
//        routeVariantService.save(routeVariant);
//        return "redirect:/manage-route-variants";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String showEditRouteVariantForm(@PathVariable("id") Long id, Model model) {
//        RouteVariant routeVariant = routeVariantService.findById(id);
//        if (routeVariant == null) {
//            model.addAttribute("msg", "Lộ trình không tồn tại.");
//            return "redirect:/manage-route-variants";
//        }
//        model.addAttribute("routeVariant", routeVariant);
//        return "route-variant/route-variant-edit";
//    }
//
//    @PostMapping("/edit/{id}")
//    public String updateRouteVariant(@PathVariable("id") Long id, RouteVariant routeVariant) {
//        routeVariant.setId(id);
//        routeVariantService.update(routeVariant);
//        return "redirect:/manage-route-variants";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteRouteVariant(@PathVariable("id") Long id) {
//        routeVariantService.delete(id);
//        return "redirect:/manage-route-variants";
//    }
//}
package com.publictransport.controllers;

import com.publictransport.models.Route;
import com.publictransport.models.RouteVariant;
import com.publictransport.models.Stop;
import com.publictransport.models.Station;
import com.publictransport.services.RouteService;
import com.publictransport.services.RouteVariantService;
import com.publictransport.services.StopService;
import com.publictransport.services.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manage-route-variants")
public class RouteVariantController {

    @Autowired
    private RouteVariantService routeVariantService;

    @Autowired
    private StopService stopService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private StationService stationService;

    @GetMapping
    public String getAllRouteVariants(Model model,
                                      @RequestParam(name = "page", defaultValue = "1") int page,
                                      @RequestParam(name = "size", defaultValue = "10") int size,
                                      @RequestParam(name = "name", required = false) String name,
                                      @RequestParam(name = "startStop", required = false) String startStop,
                                      @RequestParam(name = "endStop", required = false) String endStop) {
        List<RouteVariant> routeVariants;
        long totalItems;
        int totalPages;

        Map<String, String> params = new HashMap<>();
        if (name != null && !name.trim().isEmpty()) {
            params.put("name", name);
        }
        if (startStop != null && !startStop.trim().isEmpty()) {
            params.put("startStop", startStop);
        }
        if (endStop != null && !endStop.trim().isEmpty()) {
            params.put("endStop", endStop);
        }

        if (!params.isEmpty()) {
            routeVariants = routeVariantService.searchRouteVariants(params, page, size);
            totalItems = routeVariantService.countRouteVariantsByParams(params);
            totalPages = (int) Math.ceil((double) totalItems / size);
        } else {
            routeVariants = routeVariantService.findAllRouteVariants(page, size);
            totalItems = routeVariantService.countAllRouteVariants();
            totalPages = (int) Math.ceil((double) totalItems / size);
        }

        model.addAttribute("routeVariants", routeVariants);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("size", size);
        model.addAttribute("name", name);
        model.addAttribute("startStop", startStop);
        model.addAttribute("endStop", endStop);
        return "route-variant/route-variants";
    }

    @GetMapping("/detail/{id}")
    public String viewRouteVariantDetail(@PathVariable("id") Long id, Model model) {
        RouteVariant routeVariant = routeVariantService.findById(id);
        if (routeVariant == null) {
            model.addAttribute("msg", "Lộ trình không tồn tại.");
            return "redirect:/manage-route-variants";
        }

        List<Stop> stops = stopService.findStopsByRouteVariantId(id);

        model.addAttribute("routeVariant", routeVariant);
        model.addAttribute("stops", stops);
        return "route-variant/route-variant-detail";
    }

    @GetMapping("/add")
    public String showAddRouteVariantForm(Model model) {
        List<Route> routes = routeService.findAllRoutes(1, Integer.MAX_VALUE);
        List<Station> stations = stationService.findAllStations(1, Integer.MAX_VALUE); // Lấy tất cả Station
        model.addAttribute("routes", routes);
        model.addAttribute("stations", stations);
        model.addAttribute("routeVariant", new RouteVariant());
        return "route-variant/route-variant-add";
    }

    @PostMapping("/add")
    public String addRouteVariant(RouteVariant routeVariant, @RequestParam Map<String, String> params) {
        routeVariantService.save(routeVariant);

        // Xử lý danh sách Stop
        params.forEach((key, value) -> {
            if (key.startsWith("stops[")) {
                String index = key.substring(key.indexOf("[") + 1, key.indexOf("]"));
                if (key.endsWith(".station.id")) {
                    Long stationId = Long.parseLong(value);
                    String stopOrderKey = "stops[" + index + "].stopOrder";
                    Integer stopOrder = Integer.parseInt(params.get(stopOrderKey));

                    Stop stop = new Stop();
                    stop.setRouteVariant(routeVariant);
                    Station station = new Station();
                    station.setId(stationId);
                    stop.setStation(station);
                    stop.setStopOrder(stopOrder);

                    stopService.save(stop);
                }
            }
        });

        return "redirect:/manage-route-variants";
    }

    @GetMapping("/edit/{id}")
    public String showEditRouteVariantForm(@PathVariable("id") Long id, Model model) {
        RouteVariant routeVariant = routeVariantService.findById(id);
        if (routeVariant == null) {
            model.addAttribute("msg", "Lộ trình không tồn tại.");
            return "redirect:/manage-route-variants";
        }
        model.addAttribute("routeVariant", routeVariant);
        return "route-variant/route-variant-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateRouteVariant(@PathVariable("id") Long id, RouteVariant routeVariant) {
        routeVariant.setId(id);
        routeVariantService.update(routeVariant);
        return "redirect:/manage-route-variants";
    }

    @GetMapping("/delete/{id}")
    public String deleteRouteVariant(@PathVariable("id") Long id) {
        routeVariantService.delete(id);
        return "redirect:/manage-route-variants";
    }
}