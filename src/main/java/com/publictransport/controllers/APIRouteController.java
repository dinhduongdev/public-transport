/// /package com.publictransport.controllers;
/// /
/// /
/// /import com.publictransport.models.Route;
/// /import com.publictransport.services.RouteService;
/// /import org.springframework.beans.factory.annotation.Autowired;
/// /import org.springframework.http.ResponseEntity;
/// /import org.springframework.web.bind.annotation.*;
/// /import java.util.List;
/// /
/// /import com.publictransport.utils.Constant;
/// /@RestController
/// /@RequestMapping("/api")
/// /@CrossOrigin
/// /public class APIRouteController {
/// /    @Autowired
/// /    private RouteService routeService;
/// /
/// /    @GetMapping("/routes")
/// /    public ResponseEntity<List<Route>> list(@RequestParam(name = "page", required = false) Integer page,
/// /                                            @RequestParam(name = "size", required = false) Integer size) {
/// /        int currentPage = (page != null) ? page : Constant.DEFAULT_PAGE;
/// /        int pageSize = (size != null) ? size : Constant.DEFAULT_SIZE;
/// /        return ResponseEntity.ok(routeService.findAllRoutes(currentPage, pageSize));
/// /    }
/// /
/// /
/// /}
//
//package com.publictransport.controllers;
//
//import com.publictransport.models.*;
//import com.publictransport.services.RouteService;
//import com.publictransport.services.ScheduleService;
//import com.publictransport.services.ScheduleTripService;
//import com.publictransport.services.StopService;
//import com.publictransport.utils.Constant;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api")
//@CrossOrigin
//public class APIRouteController {
//
//    @Autowired
//    private RouteService routeService;
//    @Autowired
//    private StopService stopService;
//    @Autowired
//    private ScheduleService scheduleService;
//    @Autowired
//    private ScheduleTripService scheduleTripService;
//
//    // Get all routes with pagination and optional search parameters
//    @GetMapping("/routes")
//    public ResponseEntity<Map<String, Object>> list(
//            @RequestParam(name = "page", required = false, defaultValue ="" + Constant.DEFAULT_PAGE) Integer page,
//            @RequestParam(name = "size", required = false, defaultValue ="" + Constant.DEFAULT_SIZE) Integer size,
//            @RequestParam(name = "name", required = false) String name,
//            @RequestParam(name = "code", required = false) String code,
//            @RequestParam(name = "type", required = false) String type) {
//
//        List<Route> routes;
//        long totalItems;
//        int totalPages;
//
//        //  search parameters
//        Map<String, String> params = new HashMap<>();
//        if (name != null && !name.trim().isEmpty()) {
//            params.put("name", name);
//        }
//        if (code != null && !code.trim().isEmpty()) {
//            params.put("code", code);
//        }
//        if (type != null && !type.trim().isEmpty()) {
//            params.put("type", type);
//        }
//
//        // Perform search or fetch all routes based on parameters
//        if (!params.isEmpty()) {
//            routes = routeService.searchRoutes(params, page, size);
//            totalItems = routeService.countRoutesByParams(params);
//        } else {
//            routes = routeService.findAllRoutes(page, size);
//            totalItems = routeService.countAllRoutes();
//        }
//        totalPages = (int) Math.ceil((double) totalItems / size);
//
//        // Include route variants for each route
//        Map<Long, List<RouteVariant>> routeVariantsMap = new HashMap<>();
//        for (Route route : routes) {
//            List<RouteVariant> variants = routeService.findRouteVariantsByRouteId(route.getId());
//            routeVariantsMap.put(route.getId(), variants);
//        }
//
//        // response
//        Map<String, Object> response = new HashMap<>();
//        response.put("routes", routes);
//        response.put("routeVariantsMap", routeVariantsMap);
//        response.put("currentPage", page);
//        response.put("totalPages", totalPages);
//        response.put("totalItems", totalItems);
//        response.put("size", size);
//
//        return ResponseEntity.ok(response);
//    }
//
//    // Get route details by ID
//    @GetMapping("/routes/{id}")
//    public ResponseEntity<Object> getRouteById(@PathVariable("id") Long id) {
//        Route route = routeService.findById(id);
//        if (route == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("message", "Route not found"));
//        }
//
//        // Lấy danh sách RouteVariant (lượt đi và lượt về)
//        List<RouteVariant> variants = routeService.findRouteVariantsByRouteId(id);
//
//        // Lấy danh sách trạm dừng cho từng RouteVariant
//        Map<Long, List<Stop>> variantStopsMap = new HashMap<>();
//        for (RouteVariant variant : variants) {
//            List<Stop> stops = stopService.findStopsByRouteVariantId(variant.getId());
//            stops.sort((s1, s2) -> Integer.compare(s1.getStopOrder(), s2.getStopOrder()));
//            variantStopsMap.put(variant.getId(), stops);
//        }
//
//        // Lấy danh sách Schedule cho từng RouteVariant
//        Map<Long, List<Schedule>> variantSchedulesMap = new HashMap<>();
//        Map<Long, List<ScheduleTrip>> scheduleTripsMap = new HashMap<>();
//        for (RouteVariant variant : variants) {
//            // Tìm các Schedule liên quan đến RouteVariant
//            Map<String, String> params = new HashMap<>();
//            params.put("routeVariantId", variant.getId().toString());
//            List<Schedule> schedules = scheduleService.searchSchedules(params, 1, Integer.MAX_VALUE);
//            variantSchedulesMap.put(variant.getId(), schedules);
//
//            // Lấy danh sách ScheduleTrip cho từng Schedule
//            for (Schedule schedule : schedules) {
//                List<ScheduleTrip> trips = scheduleTripService.findByScheduleId(schedule.getId(), 1, Integer.MAX_VALUE);
//                scheduleTripsMap.put(schedule.getId(), trips);
//            }
//        }
//
//        // Chuẩn bị phản hồi
//        Map<String, Object> response = new HashMap<>();
//        response.put("route", route);
//        response.put("variants", variants);
//        response.put("variantStopsMap", variantStopsMap);
//        response.put("variantSchedulesMap", variantSchedulesMap); // Thêm schedules
//        response.put("scheduleTripsMap", scheduleTripsMap);      // Thêm trips
//        return ResponseEntity.ok(response);
//    }
//
//
//
//
//}

package com.publictransport.controllers;

import com.publictransport.dto.params.RouteFilter;
import com.publictransport.models.*;
import com.publictransport.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class APIRouteController {

    private final RouteService routeService;
    private final RouteVariantService routeVariantService;
    private final StopService stopService;
    private final ScheduleService scheduleService;
    private final ScheduleTripService scheduleTripService;

    @Autowired
    public APIRouteController(
            RouteService routeService,
            RouteVariantService routeVariantService,
            StopService stopService,
            ScheduleService scheduleService,
            ScheduleTripService scheduleTripService) {
        this.routeService = routeService;
        this.routeVariantService = routeVariantService;
        this.stopService = stopService;
        this.scheduleService = scheduleService;
        this.scheduleTripService = scheduleTripService;
    }

    @GetMapping("/routes")
    public ResponseEntity<Map<String, Object>> list(@Valid RouteFilter params) {
        long totalRoutes = routeService.countRoutes(params);
        int totalPages = (int) Math.ceil((double) totalRoutes / params.getSize());

        if (params.getPage() > totalPages) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Requested page exceeds total pages"));
        }

        List<Route> routes = routeService.findRoutes(params, true);

        Map<String, Object> response = new HashMap<>();
        response.put("routes", routes);
        response.put("currentPage", params.getPage());
        response.put("totalPages", totalPages);
        response.put("totalItems", totalRoutes);
        response.put("size", params.getSize());

        return ResponseEntity.ok(response);
    }

    // Get route details by ID
//    @GetMapping("/routes/{id}")
//    public ResponseEntity<Object> getRouteById(@PathVariable("id") Long id) {
//        Optional<Route> route = routeService.findById(id);
//        if (route.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("message", "Route not found"));
//        }
//
//        // Lấy danh sách RouteVariant (lượt đi và lượt về)
//        List<RouteVariant> variants = routeService.findRouteVariantsByRouteId(id);
//
//        // Lấy danh sách trạm dừng cho từng RouteVariant
//        Map<Long, List<Stop>> variantStopsMap = new HashMap<>();
//        for (RouteVariant variant : variants) {
//            List<Stop> stops = stopService.findStopsByRouteVariantId(variant.getId());
//            stops.sort((s1, s2) -> Integer.compare(s1.getStopOrder(), s2.getStopOrder()));
//            variantStopsMap.put(variant.getId(), stops);
//        }
//
//        // Lấy danh sách Schedule cho từng RouteVariant
//        Map<Long, List<Schedule>> variantSchedulesMap = new HashMap<>();
//        Map<Long, List<ScheduleTrip>> scheduleTripsMap = new HashMap<>();
//        for (RouteVariant variant : variants) {
//            // Tìm các Schedule liên quan đến RouteVariant
//            Map<String, String> params = new HashMap<>();
//            params.put("routeVariantId", variant.getId().toString());
//            List<Schedule> schedules = scheduleService.searchSchedules(params, 1, Integer.MAX_VALUE);
//            variantSchedulesMap.put(variant.getId(), schedules);
//
//            // Lấy danh sách ScheduleTrip cho từng Schedule
//            for (Schedule schedule : schedules) {
//                List<ScheduleTrip> trips = scheduleTripService.findByScheduleId(schedule.getId(), 1, Integer.MAX_VALUE);
//                scheduleTripsMap.put(schedule.getId(), trips);
//            }
//        }
//
//        // Chuẩn bị phản hồi
//        Map<String, Object> response = new HashMap<>();
//        response.put("route", route);
//        response.put("variants", variants);
//        response.put("variantStopsMap", variantStopsMap);
//        response.put("variantSchedulesMap", variantSchedulesMap);
//        response.put("scheduleTripsMap", scheduleTripsMap);
//        return ResponseEntity.ok(response);
//    }

}