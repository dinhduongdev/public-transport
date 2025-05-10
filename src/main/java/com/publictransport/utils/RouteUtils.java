//package com.publictransport.utils;
//
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.ui.Model;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//@Component
//@AllArgsConstructor
//public class RouteUtils {
//
//    private final RouteService routeService;
//    private final StopService stopService;
//    private final StationService stationService;
//
//    public void prepareRouteData(Long id, Model model) {
//        // Lấy thông tin tuyến đường
//        Route route = routeService.getRouteById(id);
//
//        // Lấy danh sách các điểm dừng của tuyến đường
//        List<Stop> stops = stopService.getStopsByRouteId(id);
//
//        // Lấy danh sách id của các trạm được chọn
//        List<Long> selectedStationIds = stops.stream()
//                .map(stop -> stop.getStationId().getId())
//                .collect(Collectors.toList());
//
//        // Lấy danh sách tất cả các trạm
//        List<Station> stationsList = stationService.getStations();
//
//        // Thêm các thuộc tính vào model
//        model.addAttribute("route", route);
//        model.addAttribute("selectedStationIds", selectedStationIds);
//        model.addAttribute("stationsList", stationsList);
//        if (!stationsList.isEmpty()) {
//            model.addAttribute("firstStationName", stationsList.get(0).getStopName());
//            model.addAttribute("lastStationName", stationsList.get(stationsList.size() - 1).getStopName());
//        }
//
//    }
//}