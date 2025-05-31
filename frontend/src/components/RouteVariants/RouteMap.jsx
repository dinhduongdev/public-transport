import React, { useEffect, useState, memo } from "react";
import { useDispatch, useSelector } from "react-redux";
import L from "leaflet";
import polyline from "@mapbox/polyline";
import "leaflet/dist/leaflet.css";
import { fetchTrafficReports } from "../../features/trafficreport/trafficReportsSlice";

// Fix for Leaflet default marker icons
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl:
    "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

const RouteMap = ({ stops }) => {
  const dispatch = useDispatch();
  const [routeError, setRouteError] = useState(null);
  const { reports } = useSelector((state) => state.trafficReports); // Lấy báo cáo từ Redux

  // Lấy danh sách báo cáo của người dùng
  useEffect(() => {
    dispatch(fetchTrafficReports());
  }, [dispatch]);

  useEffect(() => {
    if (!stops || stops.length === 0) return;

    // Initialize the map
    const map = L.map("map").setView(
      [stops[0].station.coordinates.lat, stops[0].station.coordinates.lng],
      13
    );

    // Add OpenStreetMap tiles
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution:
        '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(map);

    // Định nghĩa màu sắc dựa trên trạng thái
    const getStatusColor = (status) => {
      switch (status) {
        case "CLEAR":
          return { color: "green", fillColor: "green", fillOpacity: 0.5 };
        case "MODERATE":
          return { color: "yellow", fillColor: "yellow", fillOpacity: 0.5 };
        case "HEAVY":
          return { color: "orange", fillColor: "orange", fillOpacity: 0.5 };
        case "STUCK":
          return { color: "red", fillColor: "red", fillOpacity: 0.5 };
        default:
          return { color: "gray", fillColor: "gray", fillOpacity: 0.5 };
      }
    };

  const parseTimeArray = (timeArray) => {
      if (!timeArray || !Array.isArray(timeArray) || timeArray.length < 5) {
        return null;
      }
      const [year, month, day, hour, minute] = timeArray;
      // JavaScript months are 0-based, so subtract 1 from month
      return new Date(year, month - 1, day, hour, minute);
    };

  // Kiểm tra báo cáo có trong khoảng thời gian hiện tại không
  const isReportActive = (report) => {
    const startTime = parseTimeArray(report.startTime);
    const endTime = parseTimeArray(report.endTime);
    if (!startTime || !endTime) return false;
    const now = new Date();
    return now >= startTime && now <= endTime;
  };
    // Fetch route from OSRM
    const fetchRoute = async () => {
      try {
        const coords = stops
          .map(
            (stop) =>
              `${stop.station.coordinates.lng},${stop.station.coordinates.lat}`
          )
          .join(";");
        const osrmUrl = `http://router.project-osrm.org/route/v1/driving/${coords}?overview=full&geometries=polyline`;
        const response = await fetch(osrmUrl);
        if (!response.ok) {
          throw new Error("Không thể lấy tuyến đường từ OSRM");
        }
        const data = await response.json();
        if (data.code !== "Ok" || !data.routes || data.routes.length === 0) {
          throw new Error("Không tìm thấy tuyến đường hợp lệ");
        }

        const routeGeometry = polyline.decode(data.routes[0].geometry);
        const routeCoords = routeGeometry.map(([lat, lng]) => [lat, lng]);

        L.polyline(routeCoords, {
          color: "#14B8A6",
          weight: 4,
          opacity: 0.7,
        }).addTo(map);

        // Tính bounds bao gồm cả stops và reports
        const bounds = L.latLngBounds([
          ...routeCoords,
          ...stops.map((stop) => [
            stop.station.coordinates.lat,
            stop.station.coordinates.lng,
          ]),
          ...reports
            .filter((report) => report.latitude && report.longitude)
            .map((report) => [report.latitude, report.longitude]),
        ]);
        map.fitBounds(bounds, { padding: [50, 50] });
      } catch (err) {
        setRouteError(err.message);
      }
    };

    // Add markers for each stop
    stops.forEach((stop) => {
      L.marker([stop.station.coordinates.lat, stop.station.coordinates.lng])
        .addTo(map)
        .bindPopup(
          `<b>${stop.station.name}</b><br>${stop.station.location.address}`
        );
    });

    // Add red circles for traffic reports
    reports
      .filter((report) => isReportActive(report) && report.latitude && report.longitude)
      .forEach((report) => {
      if (report.latitude && report.longitude) {
        console.log(new Date());
        console.log(report.startTime);
        
        
        L.circle([report.latitude, report.longitude], {
          ...getStatusColor(report.status),
          radius: 100, // Bán kính 100 mét
        })
          .addTo(map)
          .bindPopup(
            `<div class="text-sm">
              <strong>${report.location}</strong><br>
              Mô tả: ${report.description}<br>
              Trạng thái: ${report.status}<br>
              Tọa độ: (${report.latitude}, ${report.longitude})<br>
              Thời gian: ${formatCreatedAt(report.createdAt)}
            </div>`
          );
      }
    });

    fetchRoute();

    // Cleanup on unmount
    return () => {
      map.remove();
    };
  }, [stops, reports]); // Thêm reports vào dependencies

  return (
    <div className="h-full">
      {/* {routeError && (
        <div className="bg-rose-50 border border-rose-200 text-rose-600 p-4 rounded-lg mb-2">
          Lỗi bản đồ: {routeError}
        </div>
      )} */}
      <div id="map" className="h-full w-full" />
    </div>
  );
};

// Hàm định dạng createdAt
const formatCreatedAt = (createdAt) => {
  if (!createdAt || !Array.isArray(createdAt) || createdAt.length < 6) {
    return "Không xác định";
  }
  const [year, month, day, hour, minute, second] = createdAt;
  const date = new Date(year, month - 1, day, hour, minute, second);
  return date.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
};

export default memo(RouteMap);
