package com.publictransport.controllers;

import com.publictransport.models.RouteVariant;
import com.publictransport.models.Schedule;
import com.publictransport.models.ScheduleTrip;
import com.publictransport.models.Stop;
import com.publictransport.services.RouteVariantService;
import com.publictransport.services.ScheduleService;
import com.publictransport.services.ScheduleTripService;
import com.publictransport.services.StopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/route-variants")
public class APIRouteVariantController {
    private final RouteVariantService routeVariantService;
    private final StopService stopService;
    private final ScheduleService scheduleService;
    private final ScheduleTripService scheduleTripService;

    @Autowired
    public APIRouteVariantController(
            RouteVariantService routeVariantService,
            StopService stopService,
            ScheduleService scheduleService,
            ScheduleTripService scheduleTripService) {
        this.routeVariantService = routeVariantService;
        this.stopService = stopService;
        this.scheduleService = scheduleService;
        this.scheduleTripService = scheduleTripService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRouteVariantById(@PathVariable("id") Long id) {
        // Tìm RouteVariant theo ID
        RouteVariant variant = routeVariantService.findById(id);
        if (variant == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "RouteVariant not found"));
        }

        // Lấy danh sách trạm dừng theo RouteVariant ID
        List<Stop> stops = stopService.findStopsByRouteVariantId(id);
        stops.sort(Comparator.comparingInt(Stop::getStopOrder));

        // Lấy danh sách Schedule liên quan đến RouteVariant
        Map<String, String> params = new HashMap<>();
        params.put("routeVariantId", variant.getId().toString());
        List<Schedule> schedules = scheduleService.searchSchedules(params, 1, Integer.MAX_VALUE);

        // Lấy danh sách ScheduleTrip cho từng Schedule
        Map<Long, List<ScheduleTrip>> scheduleTripsMap = new HashMap<>();
        for (Schedule schedule : schedules) {
            List<ScheduleTrip> trips = scheduleTripService.findByScheduleId(schedule.getId(), 1, Integer.MAX_VALUE);
            scheduleTripsMap.put(schedule.getId(), trips);
        }

        // Tạo phản hồi
        Map<String, Object> response = new HashMap<>();
        response.put("routeVariant", variant);
        response.put("stops", stops);
        response.put("schedules", schedules);
        response.put("scheduleTripsMap", scheduleTripsMap);

        return ResponseEntity.ok(response);
    }
}
