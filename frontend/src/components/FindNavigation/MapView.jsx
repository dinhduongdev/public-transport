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
  const { routes, selectedStopCoordinates } = useSelector(
    (state) => state.navigation
  );
  const { reports } = useSelector((state) => state.trafficReports); // Lấy báo cáo từ Redux
  const [routePaths, setRoutePaths] = useState([]);

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

  // Cập nhật đường đi khi routes thay đổi
  useEffect(() => {
    const updatePaths = async () => {
      const paths = await Promise.all(
        routes.map(async (route) => {
          const coordinates = route.hops.map((hop) => ({
            lat: hop.stop.station.coordinates.lat,
            lng: hop.stop.station.coordinates.lng,
          }));
          return await fetchRoutePath(coordinates);
        })
      );
      setRoutePaths(paths);
    };
    updatePaths();
  }, [routes]);

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
        routes={routes}
        selectedStopCoordinates={selectedStopCoordinates}
        reports={reports} // Truyền reports vào FitBounds
      />
      {routes.map((route, routeIndex) => (
        <React.Fragment key={routeIndex}>
          {/* Marker cho điểm bắt đầu */}
          <Marker
            position={[route.startCoordinates.lat, route.startCoordinates.lng]}
            icon={startIcon}
          >
            <Popup>
              <div className="text-sm font-medium text-gray-800">
                <strong>Điểm bắt đầu</strong>
                <br />
                Tọa độ: ({route.startCoordinates.lat},{" "}
                {route.startCoordinates.lng})
              </div>
            </Popup>
          </Marker>

          {/* Marker cho điểm kết thúc */}
          <Marker
            position={[route.endCoordinates.lat, route.endCoordinates.lng]}
            icon={endIcon}
          >
            <Popup>
              <div className="text-sm font-medium text-gray-800">
                <strong>Điểm kết thúc</strong>
                <br />
                Tọa độ: ({route.endCoordinates.lat}, {route.endCoordinates.lng})
              </div>
            </Popup>
          </Marker>

          {/* Marker cho các điểm dừng */}
          {route.hops.map((hop) => (
            <Marker
              key={hop.stop.id}
              position={[
                hop.stop.station.coordinates.lat,
                hop.stop.station.coordinates.lng,
              ]}
            >
              <Popup>
                <div className="text-sm">
                  <strong>{hop.stop.station.name}</strong>
                  <br />
                  {hop.stop.station.name}, {hop.stop.street}
                  <br />
                  {hop.stop.station.location.ward || "N/A"},{" "}
                  {hop.stop.station.location.zone}
                </div>
              </Popup>
            </Marker>
          ))}

          {/* Đường lối */}
          {routePaths[routeIndex] && routePaths[routeIndex]?.length > 0 && (
            <Polyline
              positions={routePaths[routeIndex]}
              color={
                routeIndex === 0
                  ? "#14B8A6"
                  : routeIndex === 1
                  ? "#F43F5E"
                  : "#3B82F6"
              }
              weight={4}
              opacity={0.7}
            />
          )}
        </React.Fragment>
      ))}

      {/* Vẽ hình tròn đỏ cho các điểm báo cáo */}
      {reports.map(
        (report) =>
          report.latitude &&
          report.longitude && (
            <Circle
              key={report.id}
              center={[report.latitude, report.longitude]}
              radius={100} // Bán kính 100 mét
              pathOptions={{
                color: "red",
                fillColor: "red",
                fillOpacity: 0.5,
              }}
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

// Hàm định dạng createdAt (tái sử dụng từ UserTrafficReports)
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
