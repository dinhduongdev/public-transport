
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  MapContainer,
  TileLayer,
  Polyline,
  Marker,
  Popup,
  Circle,
} from "react-leaflet";
import FitBounds from "./FitBounds";
import { startIcon, endIcon } from "./MapIcons";
import "leaflet/dist/leaflet.css";
import { fetchTrafficReports } from "../../features/trafficreport/trafficReportsSlice";

export default function MapView() {
  const dispatch = useDispatch();
  const { routes, selectedStopCoordinates, selectedRouteIndex } = useSelector(
    (state) => state.navigation
  );
  const { reports } = useSelector((state) => state.trafficReports);
  const [routePath, setRoutePath] = useState([]); // Chỉ lưu đường đi của tuyến được chọn

  // Lấy danh sách báo cáo của người dùng
  useEffect(() => {
    dispatch(fetchTrafficReports());
  }, [dispatch]);

  // Hàm gọi API OSRM để lấy đường đi bằng xe
  const fetchRoutePath = async (coordinates) => {
    if (coordinates.length < 2) return [];
    const coordsString = coordinates
      .map((coord) => `${coord.lng},${coord.lat}`)
      .join(";");
    const url = `http://router.project-osrm.org/route/v1/driving/${coordsString}?overview=full&geometries=geojson`;
    try {
      const response = await fetch(url);
      const data = await response.json();
      if (data.routes && data.routes.length > 0) {
        return data.routes[0].geometry.coordinates.map(([lng, lat]) => [
          lat,
          lng,
        ]);
      }
      return [];
    } catch (error) {
      console.error("Lỗi khi lấy đường đi từ OSRM:", error);
      return [];
    }
  };

  // Cập nhật đường đi khi tuyến được chọn thay đổi
  useEffect(() => {
    const updatePath = async () => {
      if (routes[selectedRouteIndex]) {
        const coordinates = routes[selectedRouteIndex].hops.map((hop) => ({
          lat: hop.stop.station.coordinates.lat,
          lng: hop.stop.station.coordinates.lng,
        }));
        const path = await fetchRoutePath(coordinates);
        setRoutePath(path);
      } else {
        setRoutePath([]);
      }
    };
    updatePath();
  }, [routes, selectedRouteIndex]);

  // Nếu không có tuyến được chọn, không hiển thị gì
  if (!routes[selectedRouteIndex]) {
    return (
      <MapContainer
        center={[10.371, 106.745]}
        zoom={13}
        style={{ height: "100%", width: "100%" }}
        className="z-0"
      >
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        />
      </MapContainer>
    );
  }

  // Chuyển đổi mảng thời gian thành đối tượng Date
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
  const selectedRoute = routes[selectedRouteIndex];

  return (
    <MapContainer
      center={[10.371, 106.745]}
      zoom={13}
      style={{ height: "100%", width: "100%" }}
      className="z-0"
    >
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />
      <FitBounds
        routes={[selectedRoute]} // Chỉ truyền tuyến được chọn
        selectedStopCoordinates={selectedStopCoordinates}
        reports={reports}
      />
      {/* Marker cho điểm bắt đầu */}
      <Marker
        position={[selectedRoute.startCoordinates.lat, selectedRoute.startCoordinates.lng]}
        icon={startIcon}
      >
        <Popup>
          <div className="text-sm font-medium text-gray-800">
            <strong>Điểm bắt đầu</strong>
            <br />
            Tọa độ: ({selectedRoute.startCoordinates.lat}, {selectedRoute.startCoordinates.lng})
          </div>
        </Popup>
      </Marker>

      {/* Marker cho điểm kết thúc */}
      <Marker
        position={[selectedRoute.endCoordinates.lat, selectedRoute.endCoordinates.lng]}
        icon={endIcon}
      >
        <Popup>
          <div className="text-sm font-medium text-gray-800">
            <strong>Điểm kết thúc</strong>
            <br />
            Tọa độ: ({selectedRoute.endCoordinates.lat}, {selectedRoute.endCoordinates.lng})
          </div>
        </Popup>
      </Marker>

      {/* Marker cho các điểm dừng */}
      {selectedRoute.hops.map((hop) => (
        <Marker
          key={hop.stop.id}
          position={[hop.stop.station.coordinates.lat, hop.stop.station.coordinates.lng]}
        >
          <Popup>
            <div className="text-sm">
              <strong>{hop.stop.station.name}</strong>
              <br />
              {hop.stop.station.name}, {hop.stop.street}
              <br />
              {hop.stop.station.location.ward || "N/A"}, {hop.stop.station.location.zone}
            </div>
          </Popup>
        </Marker>
      ))}

      {/* Đường lối */}
      {routePath.length > 0 && (
        <Polyline
          positions={routePath}
          color="#14B8A6"
          weight={4}
          opacity={0.7}
        />
      )}

      {/* Vẽ hình tròn đỏ cho các điểm báo cáo */}
      {reports
      .filter((report) => isReportActive(report) && report.latitude && report.longitude)
      .map(
        (report) =>
          report.latitude &&
          report.longitude && (
            <Circle
              key={report.id}
              center={[report.latitude, report.longitude]}
              radius={100}
              pathOptions={getStatusColor(report.status)}
            >
              <Popup>
                <div className="text-sm">
                  <strong>{report.location}</strong>
                  <br />
                  Mô tả: {report.description}
                  <br />
                  Trạng thái: {report.status}
                  <br />
                  Tọa độ: ({report.latitude}, {report.longitude})
                  <br />
                  Thời gian: {formatCreatedAt(report.createdAt)}
                </div>
              </Popup>
            </Circle>
          )
      )}
    </MapContainer>
  );
}

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