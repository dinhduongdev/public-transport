import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import L from 'leaflet';
import polyline from '@mapbox/polyline';
import 'leaflet/dist/leaflet.css';
import RouteSchedule from './RouteSchedule';
import RouteStops from './RouteStops';
import RouteInfo from './RouteInfo';
import RouteReviews from './RouteReviews';

// Fix for Leaflet default marker icons
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

// Map component to display the vehicle route
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
        // Prepare coordinates string for OSRM (lng,lat format)
        const coords = stops
          .map((stop) => `${stop.station.coordinates.lng},${stop.station.coordinates.lat}`)
          .join(';');

        // Use OSRM's public demo server (car profile)
        const osrmUrl = `http://router.project-osrm.org/route/v1/driving/${coords}?overview=full&geometries=polyline`;
        const response = await fetch(osrmUrl);
        if (!response.ok) {
          throw new Error('Failed to fetch route from OSRM');
        }
        const data = await response.json();

        if (data.code !== 'Ok' || !data.routes || data.routes.length === 0) {
          throw new Error('No valid route found');
        }

        // Decode the polyline geometry (OSRM returns encoded polyline)
        const routeGeometry = polyline.decode(data.routes[0].geometry);
        // Convert to Leaflet format [lat, lng]
        const routeCoords = routeGeometry.map(([lat, lng]) => [lat, lng]);

        // Add polyline for the vehicle route
        L.polyline(routeCoords, {
          color: 'blue',
          weight: 4,
          opacity: 0.7,
        }).addTo(map);

        // Fit map to bounds of the route
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

    // Fetch and draw the route
    fetchRoute();

    // Cleanup on component unmount
    return () => {
      map.remove();
    };
  }, [stops]);

  return (
    <div className="h-full">
      {/* {routeError && <p className="text-red-500 mb-2">Lỗi bản đồ: {routeError}</p>} */}
      <div id="map" className="h-full w-full" />
    </div>
  );
};

const RouteVariants = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { route } = location.state || {};
  const { routeVariants } = route || {};

  const [activeTab, setActiveTab] = useState('outbound');
  const [activeSubTab, setActiveSubTab] = useState('stops');
  const [scheduleData, setScheduleData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const outboundVariant = routeVariants?.find((v) => v.isOutbound);
  const returnVariant = routeVariants?.find((v) => !v.isOutbound);

  useEffect(() => {
    const fetchSchedule = async (variantId) => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(`http://localhost:8080/PublicTransport/api/route-variants/${variantId}`);
        if (!response.ok) {
          throw new Error('Failed to fetch schedule data');
        }
        const data = await response.json();
        setScheduleData(data);
      } catch (err) {
        setError(err.message);
        setScheduleData(null);
      } finally {
        setLoading(false);
      }
    };

    const variantId = activeTab === 'outbound' ? outboundVariant?.id : returnVariant?.id;
    if (variantId) {
      fetchSchedule(variantId);
    }
  }, [activeTab, outboundVariant, returnVariant]);

  const renderContent = () => {
    switch (activeSubTab) {
      case 'schedule':
        return (
          <div>
            <h3 className="text-lg font-semibold mb-2">Hôm nay</h3>
            <RouteSchedule
              scheduleData={scheduleData}
              loading={loading}
              error={error}
              activeTab={activeTab}
              outboundVariant={outboundVariant}
              returnVariant={returnVariant}
            />
          </div>
        );
      case 'stops':
        return (
          <div>
            <h3 className="text-lg font-semibold mb-2">Danh sách trạm dừng</h3>
            <RouteStops scheduleData={scheduleData} />
          </div>
        );
      case 'info':
        return (
          <div>
            <h3 className="text-lg font-semibold mb-2">Thông tin tuyến xe</h3>
            <RouteInfo routeVariant={scheduleData?.routeVariant} />
          </div>
        );
      case 'reviews':
        return (
          <div>
            <h3 className="text-lg font-semibold mb-2">Đánh giá</h3>
            <RouteReviews routeId= {route.id}  />
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="p-4 h-screen flex flex-col">
      {/* Header */}
      <div className="flex items-center mb-4">
        <button
          onClick={() => navigate('/')}
          className="mr-2 text-gray-600 hover:text-gray-800"
        >
          ←
        </button>
        <h1 className="text-2xl font-bold">Tuyến số {route?.code}</h1>
      </div>
      <h2 className="text-lg font-semibold mb-4">{route?.name}</h2>

      {/* Main Content: Info and Map */}
      <div className="flex flex-row flex-grow gap-4 overflow-hidden">
        {/* Route Info and Tabs (30%) */}
        <div className="w-3/10 flex flex-col overflow-y-auto pr-2">
          {/* Tabs: Lượt đi / Lượt về */}
          <div className="flex gap-4 mb-4">
            <button
              onClick={() => setActiveTab('outbound')}
              className={`px-4 py-2 rounded ${
                activeTab === 'outbound' ? 'bg-teal-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              Lượt đi
            </button>
            <button
              onClick={() => setActiveTab('return')}
              className={`px-4 py-2 rounded ${
                activeTab === 'return' ? 'bg-teal-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              Lượt về
            </button>
          </div>

          {/* Route Info */}
          {outboundVariant && returnVariant && (
            <div className="mb-4 p-4 border rounded-lg shadow bg-gray-50">
              <h3 className="text-lg font-semibold mb-2">
                {activeTab === 'outbound' ? 'Lượt đi' : 'Lượt về'}
              </h3>
              <p>
                <strong>Điểm đi:</strong>{' '}
                {activeTab === 'outbound' ? outboundVariant.startStop : returnVariant.startStop}
              </p>
              <p>
                <strong>Điểm đến:</strong>{' '}
                {activeTab === 'outbound' ? outboundVariant.endStop : returnVariant.endStop}
              </p>
              <p>
                <strong>Khoảng cách:</strong>{' '}
                {(
                  (activeTab === 'outbound' ? outboundVariant.distance : returnVariant.distance) / 1000
                ).toFixed(2)}{' '}
                km
              </p>
            </div>
          )}

          {/* Sub-tabs */}
          <div className="mb-4 flex gap-2 flex-wrap">
            <button
              onClick={() => setActiveSubTab('schedule')}
              className={`flex items-center gap-1 px-3 py-1 rounded text-sm ${
                activeSubTab === 'schedule'
                  ? 'bg-teal-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              <span>🕒</span>
              <span>Biểu đồ giờ</span>
            </button>
            <button
              onClick={() => setActiveSubTab('stops')}
              className={`flex items-center gap-1 px-3 py-1 rounded text-sm ${
                activeSubTab === 'stops'
                  ? 'bg-teal-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              <span>🔴</span>
              <span>Trạm dừng</span>
            </button>
            <button
              onClick={() => setActiveSubTab('info')}
              className={`flex items-center gap-1 px-3 py-1 rounded text-sm ${
                activeSubTab === 'info'
                  ? 'bg-teal-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              <span>ℹ️</span>
              <span>Thông tin</span>
            </button>
            <button
              onClick={() => setActiveSubTab('reviews')}
              className={`flex items-center gap-1 px-3 py-1 rounded text-sm ${
                activeSubTab === 'reviews'
                  ? 'bg-teal-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              <span>⭐</span>
              <span>Đánh giá</span>
            </button>
          </div>

          {/* Content based on sub-tab */}
          {renderContent()}
        </div>

        {/* Map (70%) */}
        <div className="w-7/10 h-full">
          <h3 className="text-lg font-semibold mb-2">Bản đồ tuyến đường</h3>
          {loading ? (
            <p>Đang tải bản đồ...</p>
          ) : error ? (
            <p className="text-red-500">Lỗi: {error}</p>
          ) : scheduleData?.stops ? (
            <RouteMap stops={scheduleData.stops} />
          ) : (
            <p>Không có dữ liệu bản đồ</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default RouteVariants;