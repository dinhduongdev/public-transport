import React, { useEffect, useState, memo } from 'react';
import L from 'leaflet';
import polyline from '@mapbox/polyline';
import 'leaflet/dist/leaflet.css';

// Fix for Leaflet default marker icons (already in MapIcons.js, kept here for standalone use)
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

const RouteMap = ({ stops }) => {
  const [routeError, setRouteError] = useState(null);

  useEffect(() => {
    if (!stops || stops.length === 0) return;

    // Initialize the map
    const map = L.map('map').setView(
      [stops[0].station.coordinates.lat, stops[0].station.coordinates.lng],
      13
    );

    // Add OpenStreetMap tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(map);

    // Fetch route from OSRM
    const fetchRoute = async () => {
      try {
        const coords = stops
          .map((stop) => `${stop.station.coordinates.lng},${stop.station.coordinates.lat}`)
          .join(';');
        const osrmUrl = `http://router.project-osrm.org/route/v1/driving/${coords}?overview=full&geometries=polyline`;
        const response = await fetch(osrmUrl);
        if (!response.ok) {
          throw new Error('Không thể lấy tuyến đường từ OSRM');
        }
        const data = await response.json();
        if (data.code !== 'Ok' || !data.routes || data.routes.length === 0) {
          throw new Error('Không tìm thấy tuyến đường hợp lệ');
        }

        const routeGeometry = polyline.decode(data.routes[0].geometry);
        const routeCoords = routeGeometry.map(([lat, lng]) => [lat, lng]);

        L.polyline(routeCoords, {
          color: '#14B8A6',
          weight: 4,
          opacity: 0.7,
        }).addTo(map);

        const bounds = L.latLngBounds(routeCoords);
        map.fitBounds(bounds, { padding: [50, 50] });
      } catch (err) {
        setRouteError(err.message);
      }
    };

    // Add markers for each stop
    stops.forEach((stop) => {
      L.marker([stop.station.coordinates.lat, stop.station.coordinates.lng])
        .addTo(map)
        .bindPopup(`<b>${stop.station.name}</b><br>${stop.station.location.address}`);
    });

    fetchRoute();

    // Cleanup on unmount
    return () => {
      map.remove();
    };
  }, [stops]);

  return (
    <div className="h-full">
      {routeError && (
        <div className="bg-rose-50 border border-rose-200 text-rose-600 p-4 rounded-lg mb-2">
          Lỗi bản đồ: {routeError}
        </div>
      )}
      <div id="map" className="h-full w-full" />
    </div>
  );
};

export default memo(RouteMap);