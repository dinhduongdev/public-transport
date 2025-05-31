
package com.publictransport.controllers;

import com.publictransport.dto.RouteVariantDTO;
import com.publictransport.dto.params.RouteFilter;
import com.publictransport.models.Route;
import com.publictransport.models.RouteVariant;
import com.publictransport.models.Station;
import com.publictransport.models.Stop;
import com.publictransport.services.RouteService;
import com.publictransport.services.RouteVariantService;
import com.publictransport.services.StationService;
import com.publictransport.services.StopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/manage-route-variants")
public class RouteVariantController {

    private final RouteVariantService routeVariantService;
    private final StopService stopService;
    private final RouteService routeService;
    private final StationService stationService;

    @Autowired
    public RouteVariantController(
            RouteVariantService routeVariantService,
            StopService stopService,
            RouteService routeService,
            StationService stationService) {
        this.routeVariantService = routeVariantService;
        this.stopService = stopService;
        this.routeService = routeService;
        this.stationService = stationService;
    }

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
        List<Route> routes = routeService.getAllRoutes();
        List<Station> stations = stationService.getAllStations();
        model.addAttribute("routes", routes);
        model.addAttribute("stations", stations);
        model.addAttribute("routeVariantDTO", new RouteVariantDTO());
        return "route-variant/route-variant-add";
    }

    @PostMapping("/add")
    public String addRouteVariant(RouteVariantDTO routeVariantDTO, RedirectAttributes redirectAttributes) {
        // Lấy Route dựa trên routeId từ DTO
        Optional<Route> optionalRoute = routeService.findById(routeVariantDTO.getRouteId());
        if (optionalRoute.isEmpty()) {
            redirectAttributes.addFlashAttribute("msg", "Tuyến đường không tồn tại.");
            return "redirect:/manage-route-variants/add";
        }

        // Kiểm tra số lượng RouteVariant hiện có của Route
        Route route = optionalRoute.get();
        List<RouteVariant> existingVariants = routeVariantService.findByRouteId(route.getId());
        if (!existingVariants.isEmpty()) {
            // Nếu tuyến đã có RouteVariant, không cho phép tạo thêm
            redirectAttributes.addFlashAttribute("msg", "Tuyến đường này đã có lộ trình. Không thể tạo thêm.");
            return "redirect:/manage-route-variants/add";
        }

        // Tạo RouteVariant cho Lượt đi
        RouteVariant outboundRouteVariant = new RouteVariant();
        outboundRouteVariant.setRoute(route);
        outboundRouteVariant.setIsOutbound(true);
        outboundRouteVariant.setName("Lượt đi");
        outboundRouteVariant.setDistance(routeVariantDTO.getOutboundDistance());

        // Lưu RouteVariant Lượt đi
        routeVariantService.save(outboundRouteVariant);

        // Xử lý danh sách các trạm dừng cho Lượt đi
        List<Stop> outboundStops = new ArrayList<>();
        for (RouteVariantDTO.StopDTO stopDTO : routeVariantDTO.getOutboundStops()) {
            if (stopDTO.getStationId() != null && stopDTO.getStopOrder() != null) {
                Stop stop = new Stop();
                stop.setRouteVariant(outboundRouteVariant);
                Optional<Station> station = stationService.findById(stopDTO.getStationId());
                System.out.println("station luot di : " + station.get().getName());
                stop.setStation(station.get());
                stop.setStopOrder(stopDTO.getStopOrder());
                outboundStops.add(stop);
                stopService.save(stop);
            }
        }

        // Xác định startStop và endStop cho Lượt đi
        if (!outboundStops.isEmpty()) {
            outboundStops.sort(Comparator.comparing(Stop::getStopOrder));
            Station firstStation = outboundStops.get(0).getStation();
            Station lastStation = outboundStops.get(outboundStops.size() - 1).getStation();
            System.out.println(firstStation.getName() + "============" + lastStation.getName());
            outboundRouteVariant.setStartStop(firstStation.getName());
            outboundRouteVariant.setEndStop(lastStation.getName());
            outboundRouteVariant.setStops(outboundStops);
            System.out.println("===========SAVE===========");
            routeVariantService.update(outboundRouteVariant);
        }

        // Tạo RouteVariant cho Lượt về
        RouteVariant inboundRouteVariant = new RouteVariant();
        inboundRouteVariant.setRoute(route);
        inboundRouteVariant.setIsOutbound(false);
        inboundRouteVariant.setName("Lượt về");
        inboundRouteVariant.setDistance(routeVariantDTO.getInboundDistance());

        // Lưu RouteVariant Lượt về
        routeVariantService.save(inboundRouteVariant);

        // Xử lý danh sách các trạm dừng cho Lượt về
        List<Stop> inboundStops = new ArrayList<>();
        for (RouteVariantDTO.StopDTO stopDTO : routeVariantDTO.getInboundStops()) {
            if (stopDTO.getStationId() != null && stopDTO.getStopOrder() != null) {
                Stop stop = new Stop();
                stop.setRouteVariant(inboundRouteVariant);
                Optional<Station> optStation = stationService.findById(stopDTO.getStationId());
                stop.setStation(optStation.get());
                stop.setStopOrder(stopDTO.getStopOrder());
                inboundStops.add(stop);
                stopService.save(stop);
            }
        }

        // Xác định startStop và endStop cho Lượt về
        if (!inboundStops.isEmpty()) {
            inboundStops.sort(Comparator.comparing(Stop::getStopOrder));
            Station firstStation = inboundStops.get(0).getStation();
            Station lastStation = inboundStops.get(inboundStops.size() - 1).getStation();
            inboundRouteVariant.setStartStop(firstStation.getName());
            inboundRouteVariant.setEndStop(lastStation.getName());
            inboundRouteVariant.setStops(inboundStops);
            routeVariantService.update(inboundRouteVariant);
        }

        return "redirect:/manage-route-variants";
    }

    @GetMapping("/edit/{id}")
    public String showEditRouteVariantForm(@PathVariable("id") Long id, Model model) {
        // Lấy RouteVariant theo id (có thể là Lượt đi hoặc Lượt về)
        RouteVariant routeVariant = routeVariantService.findById(id);
        if (routeVariant == null) {
            model.addAttribute("msg", "Lộ trình không tồn tại.");
            return "redirect:/manage-route-variants";
        }

        // Lấy Route của RouteVariant
        Route route = routeVariant.getRoute();

        // Lấy cả hai RouteVariant (Lượt đi và Lượt về) của cùng một tuyến đường
        List<RouteVariant> routeVariants = routeVariantService.findByRouteId(route.getId());
        RouteVariant outboundRouteVariant = routeVariants.stream()
                .filter(rv -> rv.getIsOutbound())
                .findFirst()
                .orElse(null);
        RouteVariant inboundRouteVariant = routeVariants.stream()
                .filter(rv -> !rv.getIsOutbound())
                .findFirst()
                .orElse(null);

        if (outboundRouteVariant == null || inboundRouteVariant == null) {
            model.addAttribute("msg", "Không tìm thấy lộ trình Lượt đi hoặc Lượt về.");
            return "redirect:/manage-route-variants";
        }

        // Lấy danh sách trạm dừng của Lượt đi và Lượt về
        List<Stop> outboundStops = stopService.findStopsByRouteVariantId(outboundRouteVariant.getId());
        List<Stop> inboundStops = stopService.findStopsByRouteVariantId(inboundRouteVariant.getId());

        // Tạo RouteVariantDTO để hiển thị trong form
        RouteVariantDTO routeVariantDTO = new RouteVariantDTO();
        routeVariantDTO.setRouteId(route.getId());
        routeVariantDTO.setOutboundDistance(outboundRouteVariant.getDistance());
        routeVariantDTO.setInboundDistance(inboundRouteVariant.getDistance());

        // Chuyển đổi danh sách Stop thành StopDTO cho Lượt đi
        List<RouteVariantDTO.StopDTO> outboundStopDTOs = new ArrayList<>();
        for (Stop stop : outboundStops) {
            RouteVariantDTO.StopDTO stopDTO = new RouteVariantDTO.StopDTO();
            stopDTO.setStationId(stop.getStation().getId());
            stopDTO.setStopOrder(stop.getStopOrder());
            outboundStopDTOs.add(stopDTO);
        }
        routeVariantDTO.setOutboundStops(outboundStopDTOs);

        // Chuyển đổi danh sách Stop thành StopDTO cho Lượt về
        List<RouteVariantDTO.StopDTO> inboundStopDTOs = new ArrayList<>();
        for (Stop stop : inboundStops) {
            RouteVariantDTO.StopDTO stopDTO = new RouteVariantDTO.StopDTO();
            stopDTO.setStationId(stop.getStation().getId());
            stopDTO.setStopOrder(stop.getStopOrder());
            inboundStopDTOs.add(stopDTO);
        }
        routeVariantDTO.setInboundStops(inboundStopDTOs);

        // Dữ liệu cần thiết cho form
        List<Route> routes = routeService.getAllRoutes();
        List<Station> stations = stationService.getAllStations();
        model.addAttribute("routes", routes);
        model.addAttribute("stations", stations);
        model.addAttribute("routeVariantDTO", routeVariantDTO);
        model.addAttribute("outboundRouteVariant", outboundRouteVariant);
        model.addAttribute("inboundRouteVariant", inboundRouteVariant);
        model.addAttribute("outboundStops", outboundStops);
        model.addAttribute("inboundStops", inboundStops);

        return "route-variant/route-variant-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateRouteVariant(@PathVariable("id") Long id, RouteVariantDTO routeVariantDTO, RedirectAttributes redirectAttributes) {
        // Lấy RouteVariant của Lượt đi (id được truyền vào là của Lượt đi)
        RouteVariant outboundRouteVariant = routeVariantService.findById(id);
        if (outboundRouteVariant == null) {
            redirectAttributes.addFlashAttribute("msg", "Lộ trình Lượt đi không tồn tại.");
            return "redirect:/manage-route-variants";
        }

        // Lấy Route của RouteVariant
        Route route = outboundRouteVariant.getRoute();

        // Lấy RouteVariant của Lượt về
        List<RouteVariant> routeVariants = routeVariantService.findByRouteId(route.getId());
        RouteVariant inboundRouteVariant = routeVariants.stream()
                .filter(rv -> !rv.getIsOutbound())
                .findFirst()
                .orElse(null);

        if (inboundRouteVariant == null) {
            redirectAttributes.addFlashAttribute("msg", "Lộ trình Lượt về không tồn tại.");
            return "redirect:/manage-route-variants";
        }

        // Kiểm tra danh sách trạm dừng của Lượt đi
        List<RouteVariantDTO.StopDTO> outboundStopDTOs = routeVariantDTO.getOutboundStops();
        if (outboundStopDTOs == null || outboundStopDTOs.size() < 2) {
            redirectAttributes.addFlashAttribute("msg", "Lượt đi: Phải có ít nhất 2 trạm dừng.");
            return "redirect:/manage-route-variants/edit/" + id;
        }

        // Kiểm tra xem tất cả trạm dừng của Lượt đi có stationId hợp lệ không
        boolean outboundStopsValid = outboundStopDTOs.stream()
                .allMatch(stopDTO -> stopDTO.getStationId() != null && stopDTO.getStopOrder() != null);
        if (!outboundStopsValid) {
            redirectAttributes.addFlashAttribute("msg", "Lượt đi: Tất cả các trạm dừng phải được chọn.");
            return "redirect:/manage-route-variants/edit/" + id;
        }

        // Kiểm tra danh sách trạm dừng của Lượt về
        List<RouteVariantDTO.StopDTO> inboundStopDTOs = routeVariantDTO.getInboundStops();
        if (inboundStopDTOs == null || inboundStopDTOs.size() < 2) {
            redirectAttributes.addFlashAttribute("msg", "Lượt về: Phải có ít nhất 2 trạm dừng.");
            return "redirect:/manage-route-variants/edit/" + id;
        }

        // Kiểm tra xem tất cả trạm dừng của Lượt về có stationId hợp lệ không
        boolean inboundStopsValid = inboundStopDTOs.stream()
                .allMatch(stopDTO -> stopDTO.getStationId() != null && stopDTO.getStopOrder() != null);
        if (!inboundStopsValid) {
            redirectAttributes.addFlashAttribute("msg", "Lượt về: Tất cả các trạm dừng phải được chọn.");
            return "redirect:/manage-route-variants/edit/" + id;
        }

        // Cập nhật RouteVariant Lượt đi
        outboundRouteVariant.setDistance(routeVariantDTO.getOutboundDistance());

        // Xóa các trạm dừng cũ của Lượt đi
        stopService.deleteByRouteVariantId(outboundRouteVariant.getId());

        // Thêm các trạm dừng mới cho Lượt đi
        List<Stop> outboundStops = new ArrayList<>();
        for (RouteVariantDTO.StopDTO stopDTO : outboundStopDTOs) {
            Stop stop = new Stop();
            stop.setRouteVariant(outboundRouteVariant);
            Optional<Station> station = stationService.findById(stopDTO.getStationId());
            stop.setStation(station.get());
            stop.setStopOrder(stopDTO.getStopOrder());
            outboundStops.add(stop);
            stopService.save(stop);
        }

        // Xác định startStop và endStop cho Lượt đi
        outboundStops.sort(Comparator.comparing(Stop::getStopOrder));
        Station firstOutboundStation = outboundStops.get(0).getStation();
        Station lastOutboundStation = outboundStops.get(outboundStops.size() - 1).getStation();
        outboundRouteVariant.setStartStop(firstOutboundStation.getName());
        outboundRouteVariant.setEndStop(lastOutboundStation.getName());

        // Kiểm tra nếu startStop và endStop trùng nhau
        if (firstOutboundStation.getName().equals(lastOutboundStation.getName())) {
            redirectAttributes.addFlashAttribute("msg", "Lượt đi: Bến đi và bến đến không được trùng nhau.");
            return "redirect:/manage-route-variants/edit/" + id;
        }

        routeVariantService.update(outboundRouteVariant);

        // Cập nhật RouteVariant Lượt về
        inboundRouteVariant.setDistance(routeVariantDTO.getInboundDistance());

        // Xóa các trạm dừng cũ của Lượt về
        stopService.deleteByRouteVariantId(inboundRouteVariant.getId());

        // Thêm các trạm dừng mới cho Lượt về
        List<Stop> inboundStops = new ArrayList<>();
        for (RouteVariantDTO.StopDTO stopDTO : inboundStopDTOs) {
            Stop stop = new Stop();
            stop.setRouteVariant(inboundRouteVariant);
            Optional<Station> station = stationService.findById(stopDTO.getStationId());
            stop.setStation(station.get());
            stop.setStopOrder(stopDTO.getStopOrder());
            inboundStops.add(stop);
            stopService.save(stop);
        }

        // Xác định startStop và endStop cho Lượt về
        inboundStops.sort(Comparator.comparing(Stop::getStopOrder));
        Station firstInboundStation = inboundStops.get(0).getStation();
        Station lastInboundStation = inboundStops.get(inboundStops.size() - 1).getStation();
        inboundRouteVariant.setStartStop(firstInboundStation.getName());
        inboundRouteVariant.setEndStop(lastInboundStation.getName());

        // Kiểm tra nếu startStop và endStop trùng nhau
        if (firstInboundStation.getName().equals(lastInboundStation.getName())) {
            redirectAttributes.addFlashAttribute("msg", "Lượt về: Bến đi và bến đến không được trùng nhau.");
            return "redirect:/manage-route-variants/edit/" + id;
        }

        routeVariantService.update(inboundRouteVariant);

        return "redirect:/manage-route-variants";
    }


    @GetMapping("/delete/{id}")
    public String deleteRouteVariant(@PathVariable("id") Long id) {
        routeVariantService.delete(id);
        return "redirect:/manage-route-variants";
    }
}