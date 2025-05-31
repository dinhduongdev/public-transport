package com.publictransport.services.impl;

import com.publictransport.annotations.RebuildRouteNavigationFilterIfKeywordsSet;
import com.publictransport.dto.params.RouteNavigationFilter;
import com.publictransport.dto.routenavigation.Hop;
import com.publictransport.dto.routenavigation.RouteDTO;
import com.publictransport.dto.routenavigation.RouteNavigation;
import com.publictransport.models.Coordinates;
import com.publictransport.models.RouteVariant;
import com.publictransport.models.Stop;
import com.publictransport.proxies.MapProxy;
import com.publictransport.services.RouteNavigationService;
import com.publictransport.services.RouteVariantService;
import com.publictransport.services.StationService;
import com.publictransport.services.StopService;
import com.publictransport.utils.MapUtils;
import com.publictransport.utils.dijkstra.Edge;
import com.publictransport.utils.dijkstra.Graph;
import com.publictransport.utils.dijkstra.Node;
import jakarta.validation.Valid;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RouteNavigationServiceImpl implements RouteNavigationService {

    private final StationService stationService;
    private final StopService stopService;
    private final MapProxy mapProxy;
    private final RouteVariantService routeVariantService;
    private final double START_RADIUS = 1.0; // Bán kính tìm kiếm trạm dừng gần điểm đi/đến (km)
    private final double END_RADIUS = 0.6;

    @Autowired
    public RouteNavigationServiceImpl(
            StationService stationService,
            RouteVariantService routeVariantService,
            StopService stopService,
            MapProxy mapProxy
    ) {
        this.stationService = stationService;
        this.stopService = stopService;
        this.routeVariantService = routeVariantService;
        this.mapProxy = mapProxy;
    }

    private Optional<Stop> findNearestGivenStopsOfRouteVar(RouteVariant routeVar, List<Stop> stops, Coordinates coords) {
        // Tìm trạm dừng gần nhất của routeVar trong danh sách các trạm dừng được cung cấp
        // Lý do: vì đã có danh sách các trạm dừng ở một khu vực nhất định
        // nên không cần phải tìm kiếm tất cả các trạm dừng của routeVar
        return stops.stream()
                .filter(stop -> routeVar.getStops().contains(stop))
                .min(Comparator.comparingDouble(stop -> MapUtils.haversineDistance(coords, stop.getStation().getCoordinates())));
    }

    private List<Stop> findNearestGivenStopsForEachRouteVar(Coordinates coords, List<Stop> stops) {
        // Nếu có 2 trạm dừng cùng một routeVar thì lấy trạm dừng gần nhất với điểm đến
        // Chọn trạm dừng gần điểm đến nhất cho mỗi routeVariant
        Map<Long, Stop> bestStopByRouteVariant = new HashMap<>();
        for (Stop stop : stops) {
            long routeVarId = stop.getRouteVariant().getId();
            double newDistance = MapUtils.haversineDistance(coords, stop.getStation().getCoordinates());

            Stop currentBest = bestStopByRouteVariant.get(routeVarId);
            if (currentBest == null || MapUtils.haversineDistance(coords, currentBest.getStation().getCoordinates()) > newDistance) {
                bestStopByRouteVariant.put(routeVarId, stop);
            }
        }
        return new ArrayList<>(bestStopByRouteVariant.values());
    }

    @Override
    @Transactional
    @RebuildRouteNavigationFilterIfKeywordsSet
    public List<RouteNavigation> findDirectRouteNavigations(@Valid RouteNavigationFilter filter) {
        // 1. tìm các điểm dừng gần điểm đi và đến. Có thể chỉnh size,radius lớn hơn để lấy nhiều trạm hơn
        var startStops = stopService.findNearbyStops(filter.getStartCoords(), START_RADIUS, Integer.MAX_VALUE);
        var endStops = stopService.findNearbyStops(filter.getEndCoords(), END_RADIUS, Integer.MAX_VALUE);

        if (startStops.isEmpty() || endStops.isEmpty()) {
            // Không có trạm dừng gần điểm đi/đến
            return List.of();
        }

        // 2. Tìm các routeVar chung giữa 2 tập hợp, từ đó lọc các stops có routeVar nằm trong tập hợp chung
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
        List<RouteVariant> commonRouteVarsWithFetchedStops =
                routeVariantService.fetchRouteVariantsWithStops(
                        commonRouteVars.stream().map(RouteVariant::getId).collect(Collectors.toSet())
                );

        // 3. Tìm trạm dừng của các routeVar chung
        List<RouteNavigation> navigations = new ArrayList<>();
        Coordinates startCoords = MapUtils.convertToCoordinates(filter.getStartCoords()).orElseThrow();
        Coordinates endCoords = MapUtils.convertToCoordinates(filter.getEndCoords()).orElseThrow();
        String formattedStartAddress = mapProxy.getAddress(startCoords).orElse("");
        String formattedEndAddress = mapProxy.getAddress(endCoords).orElse("");
        for (RouteVariant routeVar : commonRouteVarsWithFetchedStops) {
            RouteNavigation navigation = new RouteNavigation();
            navigation.setStartCoordinates(startCoords);
            navigation.setEndCoordinates(endCoords);

            // Lấy danh sách các trạm dừng của route variant
            List<Stop> stops = routeVar.getStops();

            // Tìm trạm dừng gần nhất với điểm xuất phát của người dùng
            // Giải thích:
            // - Các trạm dừng của variants có thể khác nhau (ví dụ lượt đi đi qua đường 1 chiều thì lượt về sẽ đi qua đường khác)
            // - vẫn có trường hợp dùng chung trạm, lúc đó thì thứ tự stops sẽ khác nhau
            // - ví dụ: a -> b -> c (lượt đi) và c -> b -> a (lượt về)
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
            System.out.println("Creating hops for route variant: " + routeVar.getId());
            int order = 1;
            for (int i = startIndex; i <= endIndex; i++) {
                Stop currentStop = stops.get(i);
                double nextStopDistance = 0.0;
                if (i < endIndex) {
                    // Tính khoảng cách đến trạm tiếp theo
                    Stop nextStop = stops.get(i + 1);
                    nextStopDistance = MapUtils.haversineDistance(
                            currentStop.getStation().getCoordinates(),
                            nextStop.getStation().getCoordinates()
                    ) * 1000; // Chuyển đổi sang mét
                }
                navigation.addHop(new Hop(order++, new RouteDTO(routeVar.getRoute()), currentStop, nextStopDistance));
            }

            // Tính toán khoảng cách và thời gian
            double distanceToStartStop = MapUtils.haversineDistance(
                    startCoords, nearestStartStop.get().getStation().getCoordinates()) * 1000; // Chuyển đổi sang mét
            double distanceToEndStop = MapUtils.haversineDistance(
                    endCoords, nearestEndStop.get().getStation().getCoordinates()) * 1000; // Chuyển đổi sang mét
            double totalDistanceInMeters = navigation.calculateTotalDistance() + distanceToStartStop + distanceToEndStop;
            navigation.setTotalDistanceInMeters(totalDistanceInMeters);
            navigation.setFormattedStartAddress(formattedStartAddress);
            navigation.setFormattedEndAddress(formattedEndAddress);
            navigation.setTransitCount(1); // Chỉ có một chuyến đi trong route variant
            navigations.add(navigation);
        }

        // Sắp xếp kết quả theo khoảng cách
        navigations.sort(Comparator.comparing(RouteNavigation::calculateTotalDistance));
        return navigations;
    }


    // hàm này sẽ tìm kiếm các route navigations bằng dijkstra
    // dựa trên bộ lọc đã cho có thể có chuyển tuyến
    @Transactional
    @RebuildRouteNavigationFilterIfKeywordsSet
    public List<RouteNavigation> findRouteNavigations(@Valid RouteNavigationFilter filter) {
        // 1. tìm các stops gần điểm đi, đến
        var startStops = stopService.findNearbyStops(filter.getStartCoords(), START_RADIUS, Integer.MAX_VALUE);
        var endStops = stopService.findNearbyStops(filter.getEndCoords(), END_RADIUS, Integer.MAX_VALUE);
        Coordinates startCoords = MapUtils.convertToCoordinates(filter.getStartCoords()).orElseThrow();
        Coordinates endCoords = MapUtils.convertToCoordinates(filter.getEndCoords()).orElseThrow();

        if (startStops.isEmpty() || endStops.isEmpty()) {
            return List.of();
        }

        // 2. tìm các stops của routeVar để tạo node
        // Ý tưởng: lấy tất cả các stops của các routeVar gần điểm đi & đến tạo thành node
        // sau đó tạo các edges dựa trên stopOrder của các stops
        Set<RouteVariant> candidateRouteVars = Stream.concat(
                startStops.stream().map(Stop::getRouteVariant),
                endStops.stream().map(Stop::getRouteVariant)
        ).collect(Collectors.toSet());
        List<RouteVariant> candidateRouteVarsWithFetchedStops =
                routeVariantService.fetchRouteVariantsWithStops(
                        candidateRouteVars.stream().map(RouteVariant::getId).collect(Collectors.toSet())
                );

        Graph graph = new Graph();
        for (RouteVariant routeVar : candidateRouteVarsWithFetchedStops) {
            Node previousNode = null;
            for (Stop stop : routeVar.getStops()) {
                Node currentNode = graph.addNode(stop);
                if (previousNode != null) {
                    // Tạo edge từ previousNode đến currentNode
                    // nẾU lÀ stop ĐẦu tiÊn thÌ khÔng cÓ cẠNH nÀO ĐẰng trƯỚc
                    // mặc định sẽ dùng harvesine distance để tính trọng số
                    graph.addEdge(previousNode, currentNode);
                }
                previousNode = currentNode;
            }
        }

        // 3. Thêm cạnh cho các stop có chung trạm
        Map<Long, List<Node>> stationIdToNodes = new HashMap<>();

        for (Node node : graph.getMap().values()) {
            long stationId = node.getStop().getStation().getId();
            stationIdToNodes
                    .computeIfAbsent(stationId, k -> new ArrayList<>())
                    .add(node);
        }

        // duyệt từng trạm với val là mảng các node có cùng trạm
        for (List<Node> nodeList : stationIdToNodes.values()) {
            if (nodeList.size() <= 1) continue;

            for (int i = 0; i < nodeList.size(); i++) {
                Node from = nodeList.get(i);
                for (int j = i + 1; j < nodeList.size(); j++) {
                    Node to = nodeList.get(j);

                    // 2 chiều (1000 là penalty cho việc chờ giữa các trạm)
                    graph.addEdge(from, to, 1000);
                    graph.addEdge(to, from, 1000);
                }
            }
        }

        // 3.b Tạo các cạnh giữa các trạm dừng gần nhau trong bán kính 100m

        // 4. chay dijkstra từ các node gần điểm đi
        Map<Long, Double> cost = new HashMap<>();
        Map<Long, Long> previous = new HashMap<>();
        PriorityQueue<Pair<Long, Double>> queue = new PriorityQueue<>(Comparator.comparingDouble(Pair::getRight));

        // Khoi tao cost và previous cho tất cả các node
        for (Node node : graph.getMap().values()) {
            long stopId = node.getStop().getId();
            cost.put(stopId, Double.POSITIVE_INFINITY);
            previous.put(stopId, null);
        }

        startStops = findNearestGivenStopsForEachRouteVar(startCoords, startStops);
        for (Stop startStop : startStops) {
            double initCost = MapUtils.haversineDistance(startCoords, startStop.getStation().getCoordinates());
            long stopId = startStop.getId();
            cost.put(stopId, initCost);
            queue.add(new ImmutablePair<>(stopId, initCost));
        }

        while (!queue.isEmpty()) {
            Pair<Long, Double> pair = queue.poll();
            long currentId = pair.getLeft();
            double currentCost = pair.getRight();

            if (currentCost > cost.get(currentId)) {
                continue; // Đã tìm thấy đường đi ngắn hơn đến node này
            }

            Node currentNode = graph.getNode(currentId);

            for (Edge edge : currentNode.getEdges()) {
                long neighborId = edge.getTarget().getStop().getId();
                double newCost = currentCost + edge.getWeight();

                if (newCost < cost.get(neighborId)) {
                    cost.put(neighborId, newCost);
                    previous.put(neighborId, currentId);
                    queue.add(new ImmutablePair<>(neighborId, newCost));
                }
            }
        }

        // 5. Tạo danh sách các route navigation từ kết quả dijkstra
        // chỉ lấy các endStops có thể đến được.
        // Chọn trạm dừng gần điểm đến nhất cho mỗi routeVariant
        // Eg. Nếu có 2 trạm dừng cùng một routeVar thì lấy trạm dừng gần nhất với điểm đến

        // Lọc các endStops có thể đến được
        endStops = endStops.stream()
                .filter(stop -> cost.get(stop.getId()) < Double.POSITIVE_INFINITY)
                .toList();

        // Sắp xếp các trạm dừng theo khoảng cách đến điểm đến
        endStops = findNearestGivenStopsForEachRouteVar(endCoords, endStops);


        // bắt đầu build kết quả
        String formattedStartAddress = mapProxy.getAddress(startCoords).orElse("");
        String formattedEndAddress = mapProxy.getAddress(endCoords).orElse("");
        List<RouteNavigation> navigations = new ArrayList<>();
        for (Stop endStopCandidate : endStops) {
            Long endStopId = endStopCandidate.getId();
            LinkedList<Node> pathNodes = new LinkedList<>();

            Long currentNodeId = endStopId;
            int transitCount = 0;
            long latestRouteVariantId = -1;
            while (currentNodeId != null && transitCount <= filter.getMaxNumOfTrip()) {
                Node currentNode = graph.getNode(currentNodeId);
                long currentRouteVariantId = currentNode.getStop().getRouteVariant().getId();
                if (currentRouteVariantId != latestRouteVariantId) {
                    transitCount++;
                    latestRouteVariantId = currentRouteVariantId;
                }
                pathNodes.addFirst(currentNode);
                currentNodeId = previous.get(currentNodeId);
            }

            if (pathNodes.isEmpty() || transitCount > filter.getMaxNumOfTrip()) {
                continue;
            }

            RouteNavigation navigation = new RouteNavigation();
            navigation.setStartCoordinates(startCoords);
            navigation.setEndCoordinates(endCoords);
            navigation.setTransitCount(transitCount);

            int hopOrder = 1;
            double distanceToStartStop = MapUtils.haversineDistance(
                    startCoords, pathNodes.getFirst().getStop().getStation().getCoordinates()) * 1000; // Chuyển đổi sang mét
            double distanceToEndStop = MapUtils.haversineDistance(
                    endCoords, pathNodes.getLast().getStop().getStation().getCoordinates()) * 1000; // Chuyển đổi sang mét
            double distanceAccum = distanceToStartStop + distanceToEndStop;
            for (int i = 0; i < pathNodes.size(); i++) {
                Node pathNode = pathNodes.get(i);
                Stop stopForHop = pathNode.getStop();
                double distanceToNextHop = 0.0;

                if (i < pathNodes.size() - 1) {
                    Stop nextStop = pathNodes.get(i + 1).getStop();
                    distanceToNextHop = (MapUtils.haversineDistance(stopForHop.getStation().getCoordinates(), nextStop.getStation().getCoordinates())) * 1000;
                }
                distanceAccum += distanceToNextHop;
                navigation.addHop(new Hop(hopOrder++, new RouteDTO(stopForHop.getRouteVariant().getRoute()), stopForHop, distanceToNextHop));
            }
            navigation.setTotalDistanceInMeters(distanceAccum);
            navigation.setFormattedStartAddress(formattedStartAddress);
            navigation.setFormattedEndAddress(formattedEndAddress);
            navigations.add(navigation);
        }

        if (navigations.isEmpty()) {
            // Không tìm thấy route navigation nào
            return List.of();
        }

        // Sort results by total distance
        navigations.sort(Comparator.comparingDouble(RouteNavigation::getTotalDistanceInMeters).thenComparing(RouteNavigation::getTransitCount));

        final double MAX_OFFSET_RATIO = 1.6; // Tỷ lệ tối đa cho phép so với khoảng cách ngắn nhất
        final double MIN_DISTANCE = navigations.get(0).getTotalDistanceInMeters();
        // Lọc các route navigation có tổng khoảng cách vượt quá tỷ lệ cho phép
        return navigations.stream()
                .filter(nav -> nav.getTotalDistanceInMeters() <= MIN_DISTANCE * MAX_OFFSET_RATIO)
                .collect(Collectors.toList());
    }
}
