package com.publictransport.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RouteVariantDTO {
    private Long routeId; // ID của tuyến đường

    // Thông tin cho Lượt đi
    private Float outboundDistance; // Khoảng cách của Lượt đi
    private List<StopDTO> outboundStops = new ArrayList<>(); // Danh sách trạm dừng của Lượt đi

    // Thông tin cho Lượt về
    private Float inboundDistance; // Khoảng cách của Lượt về
    private List<StopDTO> inboundStops = new ArrayList<>(); // Danh sách trạm dừng của Lượt về

    // Khởi tạo sẵn 2 trạm dừng cho cả Lượt đi và Lượt về
    public RouteVariantDTO() {
        // Khởi tạo 2 trạm dừng cho Lượt đi
        StopDTO stop1 = new StopDTO();
        stop1.setStopOrder(1);
        outboundStops.add(stop1);

        StopDTO stop2 = new StopDTO();
        stop2.setStopOrder(2);
        outboundStops.add(stop2);

        // Khởi tạo 2 trạm dừng cho Lượt về
        StopDTO stop3 = new StopDTO();
        stop3.setStopOrder(1);
        inboundStops.add(stop3);

        StopDTO stop4 = new StopDTO();
        stop4.setStopOrder(2);
        inboundStops.add(stop4);
    }

    @Getter
    @Setter
    public static class StopDTO {
        private Long stationId;
        private Integer stopOrder;
    }
}