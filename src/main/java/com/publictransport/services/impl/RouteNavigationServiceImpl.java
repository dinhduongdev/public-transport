package com.publictransport.services.impl;

import com.publictransport.dto.params.RouteNavigationFilter;
import com.publictransport.dto.params.StationFilter;
import com.publictransport.dto.routenavigation.Hop;
import com.publictransport.dto.routenavigation.RouteDTO;
import com.publictransport.dto.routenavigation.RouteNavigation;
import com.publictransport.models.Coordinates;
import com.publictransport.models.Route;
import com.publictransport.models.RouteVariant;
import com.publictransport.models.Stop;
import com.publictransport.services.RouteNavigationService;
import com.publictransport.services.RouteVariantService;
import com.publictransport.services.StationService;
import com.publictransport.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteNavigationServiceImpl implements RouteNavigationService {

    private final StationService stationService;
    private final RouteVariantService routeVariantService;

    @Autowired
    public RouteNavigationServiceImpl(
            StationService stationService,
            RouteVariantService routeVariantService) {
        this.stationService = stationService;
        this.routeVariantService = routeVariantService;
    }

    @Override
    @Transactional
    public List<RouteNavigation> findDirectRouteNavigations(RouteNavigationFilter filter) {
        // Kiểm tra xem từ khóa có được set không
        if (filter.areKeywordsSet()) {
            // Nếu có, thì build filter mới từ từ khóa
            var optNewFilter = buildNewFilterByKeyword(filter);
            if (optNewFilter.isEmpty()) {
                return List.of();
            }
            filter = optNewFilter.get();
        }
        // 1. tìm trạm gần điểm đi và đến. Có thể chỉnh size,radius lớn hơn để lấy nhiều trạm hơn
        StationFilter startStationFilter = new StationFilter();
        startStationFilter.setLatLng(filter.getStartCoords());
        startStationFilter.setRadiusKm(2.0);
        startStationFilter.setSize(Integer.MAX_VALUE);
        StationFilter endStationFilter = new StationFilter();
        endStationFilter.setLatLng(filter.getEndCoords());
        endStationFilter.setRadiusKm(0.5);
        endStationFilter.setSize(Integer.MAX_VALUE);
        var startStations = stationService.findStations(startStationFilter, true);
        var endStations = stationService.findStations(endStationFilter, true);

        if (startStations.isEmpty() || endStations.isEmpty()) {
            return List.of();
        }

        // 2. Lấy tất cả các điểm dừng (đi kèm với routeVar vì là n-1) gần trạm xuất phát
        var startStops = startStations.stream()
                .flatMap(station -> station.getStops().stream())
                .toList();

        // 3. Lấy tất cả các các điểm dừng (đi kèm với routeVar vì là n-1) gần trạm keest thúc
        var endStops = endStations.stream()
                .flatMap(station -> station.getStops().stream())
                .toList();

        // 4. Tìm các routeVar chung giữa 2 tập hợp, từ đó lọc các stops có routeVar nằm trong tập hợp chung
        Set<RouteVariant> startRouteVars = startStops.stream()
                .map(Stop::getRouteVariant)
                .collect(Collectors.toSet());
        Set<RouteVariant> endRouteVars = endStops.stream()
                .map(Stop::getRouteVariant)
                .collect(Collectors.toSet());
        Set<RouteVariant> commonRouteVars = new HashSet<>(startRouteVars);
        commonRouteVars.retainAll(endRouteVars);

        // lọc stops để chỉ lấy những stop có routeVar nằm trong tập hợp chung
        startStops = startStops.stream()
                .filter(stop -> commonRouteVars.contains(stop.getRouteVariant()))
                .collect(Collectors.toList());
        endStops = endStops.stream()
                .filter(stop -> commonRouteVars.contains(stop.getRouteVariant()))
                .collect(Collectors.toList());

        if (commonRouteVars.isEmpty() || startStops.isEmpty() || endStops.isEmpty()) {
            // Không có route variant chung hoặc không có trạm dừng gần điểm đi/đến
            return List.of();
        }

        // 5. Tìm trạm dừng của các routeVar chung
        List<RouteNavigation> routeNavigations = new ArrayList<>();
        for (RouteVariant routeVar : commonRouteVars) {
            RouteNavigation routeNavigation = new RouteNavigation();
            Route route = routeVar.getRoute();
            routeNavigation.setRoute(new RouteDTO(route.getId(), route.getCode(), route.getName(), route.getType()));
            routeNavigation.setStartCoordinates(MapUtils.convertToCoordinates(filter.getStartCoords()).get());
            routeNavigation.setEndCoordinates(MapUtils.convertToCoordinates(filter.getEndCoords()).get());

            // Lấy danh sách các trạm dừng của route variant
            List<Stop> stops = routeVar.getStops();

            // Tìm trạm dừng gần nhất với điểm xuất phát của người dùng
            Coordinates startCoords = routeNavigation.getStartCoordinates();
            Coordinates endCoords = routeNavigation.getEndCoordinates();
            Optional<Stop> nearestStartStop = findNearestGivenStopsOfRouteVar(routeVar, startStops, startCoords);
            Optional<Stop> nearestEndStop = findNearestGivenStopsOfRouteVar(routeVar, endStops, endCoords);

            // Xác định vị trí của trạm xuất phát và trạm đến trong danh sách stops
            int startIndex = stops.indexOf(nearestStartStop.get());
            int endIndex = stops.indexOf(nearestEndStop.get());

            // Nếu trạm đến ở trước trạm đi trong danh sách, thì route variant không phù hợp
            if (startIndex >= endIndex) {
                continue;
            }

            // Tạo danh sách các hop từ trạm xuất phát đến trạm đích
            int order = 1;
            double meanDistance = routeVar.getDistance() / stops.size();
            for (int i = startIndex; i <= endIndex; i++) {
                Stop currentStop = stops.get(i);
                routeNavigation.addHop(new Hop(order++, currentStop, i != endIndex ? meanDistance : 0.0));
            }

            routeNavigations.add(routeNavigation);
        }

        // Sắp xếp kết quả theo khoảng cách
        routeNavigations.sort(Comparator.comparing(RouteNavigation::calculateTotalDistance));

        return routeNavigations;
    }

    private Optional<Stop> findNearestGivenStopsOfRouteVar(RouteVariant routeVar, List<Stop> stops, Coordinates coords) {
        // Tìm trạm dừng gần nhất của routeVar trong danh sách các trạm dừng được cung cấp
        // Lý do: vì đã có danh sách các trạm dừng ở một khu vực nhất định
        // nên không cần phải tìm kiếm tất cả các trạm dừng của routeVar
        return stops.stream()
                .filter(stop -> routeVar.getStops().contains(stop))
                .min(Comparator.comparingDouble(stop -> MapUtils.haversineDistance(coords, stop.getStation().getCoordinates())));
    }

    @Override
    public Optional<RouteNavigationFilter> buildNewFilterByKeyword(RouteNavigationFilter oldFilter) {
        // build filter mới từ filter cũ giữ nguyên (maxNumOfTrip) và lấy tọa độ từ proxy
        var optStartStationFilter = stationService.buildNewFilterByKeyword(oldFilter.getStartKw());
        var optEndStationFilter = stationService.buildNewFilterByKeyword(oldFilter.getEndKw());
        if (optStartStationFilter.isEmpty() || optEndStationFilter.isEmpty()) {
            return Optional.empty();
        }
        var newFilter = new RouteNavigationFilter();
        newFilter.setStartCoords(optStartStationFilter.get().getLatLng());
        newFilter.setEndCoords(optEndStationFilter.get().getLatLng());
        newFilter.setMaxNumOfTrip(oldFilter.getMaxNumOfTrip());
        return Optional.of(newFilter);
    }
}
