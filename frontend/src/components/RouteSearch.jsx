import React, { useState, useEffect } from "react";

// RouteSearch component for the sidebar
function RouteSearch({ busRoutes, routeVariantsMap, onRouteClick }) {
  const [searchTerm, setSearchTerm] = useState('');

  // Lọc tuyến xe dựa trên từ khóa tìm kiếm
  const filteredRoutes = busRoutes.filter(route =>
    route.number.toLowerCase().includes(searchTerm.toLowerCase()) ||
    route.route.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="col-span-1 bg-white border-r overflow-y-auto p-4">
      <h2 className="text-xl font-semibold mb-4">TRA CỨU</h2>
      <input
        type="text"
        placeholder="Tìm tuyến xe"
        className="w-full p-2 mb-4 border rounded"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />
      <div className="space-y-4">
        {filteredRoutes.map((route) => {
          const variants = routeVariantsMap[route.id] || [];
          return (
            <div
              key={route.id}
              onClick={() => onRouteClick(route.id)}
              className="border rounded-lg p-3 shadow hover:shadow-md transition cursor-pointer"
            >
              <h3 className="text-green-600 font-semibold">
                Tuyến số {route.number}
              </h3>
              <p>{route.route}</p>
              {variants.map(variant => (
                <div key={variant.id} className="mt-2">
                  <p className="text-sm text-gray-600">
                    <strong>{variant.name}:</strong> {variant.startStop} → {variant.endStop}
                  </p>
                </div>
              ))}
              <div className="flex justify-between text-sm text-gray-600 mt-1">
                <span>🕒 {route.time}</span>
                <span>💰 {route.price}</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

// RouteDetail component to display route details
function RouteDetail({ route, variants, variantStopsMap, variantSchedulesMap, scheduleTripsMap, onBack }) {
  const [viewMode, setViewMode] = useState('outbound');

  // Lọc RouteVariant theo chế độ xem
  const selectedVariant = variants.find(variant =>
    viewMode === 'outbound' ? variant.isOutbound : !variant.isOutbound
  );

  // Lấy danh sách Schedule và ScheduleTrip cho RouteVariant được chọn
  const schedules = selectedVariant ? variantSchedulesMap[selectedVariant.id] || [] : [];
  const trips = schedules.flatMap(schedule => scheduleTripsMap[schedule.id] || []);
  console.log(trips);
  


  return (
    <div className="col-span-1 bg-white border-r overflow-y-auto p-4">
      <button
        onClick={onBack}
        className="mb-4 px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
      >
        Quay lại
      </button>
      <h2 className="text-xl font-semibold mb-4">CHI TIẾT TUYẾN XE</h2>
      <div className="border rounded-lg p-3 shadow">
        <h3 className="text-green-600 font-semibold">
          Tuyến số {route.code}
        </h3>
        <p className="mb-2">{route.name}</p>
        <p className="text-sm text-gray-600 mb-2"><strong>Loại:</strong> {route.type}</p>

        {/* Nút chọn lượt đi và lượt về */}
        <div className="flex gap-2 mb-4">
          <button
            onClick={() => setViewMode('outbound')}
            className={`px-4 py-2 rounded text-white ${
              viewMode === 'outbound' ? 'bg-green-600' : 'bg-gray-400'
            } hover:bg-green-700`}
          >
            Xem lượt đi
          </button>
          <button
            onClick={() => setViewMode('inbound')}
            className={`px-4 py-2 rounded text-white ${
              viewMode === 'inbound' ? 'bg-green-600' : 'bg-gray-400'
            } hover:bg-green-700`}
          >
            Xem lượt về
          </button>
        </div>

        {/* Hiển thị thông tin lộ trình và trạm dừng */}
        {selectedVariant ? (
          <div className="mt-4">
            <h4 className="text-md font-medium">{selectedVariant.name}</h4>
            <p className="text-sm text-gray-600">
              <strong>Khoảng cách:</strong> {(selectedVariant.distance / 1000).toFixed(2)} km
            </p>
            <p className="text-sm text-gray-600">
              <strong>Bến đầu:</strong> {selectedVariant.startStop}
            </p>
            <p className="text-sm text-gray-600">
              <strong>Bến cuối:</strong> {selectedVariant.endStop}
            </p>
            <h5 className="text-sm font-medium mt-2">Danh sách trạm dừng:</h5>
            {variantStopsMap[selectedVariant.id]?.length > 0 ? (
              <ul className="list-disc pl-5 text-sm text-gray-600">
                {variantStopsMap[selectedVariant.id].map(stop => (
                  <li key={stop.id}>
                    {stop.station.name} (Thứ tự: {stop.stopOrder})
                  </li>
                ))}
              </ul>
            ) : (
              <p className="text-sm text-gray-600">Không có trạm dừng nào.</p>
            )}
            <h5 className="text-sm font-medium mt-4">Lịch trình:</h5>
            {trips.length > 0 ? (
              <div className="mt-2 flex flex-wrap gap-2">
                {trips.map(trip => {              
                  return (
                    <div
                      key={trip.id}
                      className="p-2 bg-gray-200 border rounded text-sm text-center w-32"
                    >
                      <p><strong>Bắt đầu:</strong> [{trip.startTime[0]}, {trip.startTime[1]}]</p>
                      <p><strong>Kết thúc:</strong> [{trip.endTime[0]}, {trip.endTime[1]}]</p>
                    </div>
                  );
                })}
              </div>
            ) : (
              <p className="text-sm text-gray-600">Không có chuyến xe nào để hiển thị.</p>
            )}
          </div>
        ) : (
          <p className="text-sm text-gray-600">Không có lộ trình nào để hiển thị.</p>
        )}
      </div>
    </div>
  );
}

// Main BusMapUI component
export default function BusMapUI() {
  const [busRoutes, setBusRoutes] = useState([]);
  const [routeVariantsMap, setRouteVariantsMap] = useState({});
  const [selectedRoute, setSelectedRoute] = useState(null);
  const [selectedVariants, setSelectedVariants] = useState([]);
  const [selectedVariantStopsMap, setSelectedVariantStopsMap] = useState({});
  const [variantSchedulesMap, setVariantSchedulesMap] = useState({});
  const [scheduleTripsMap, setScheduleTripsMap] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch danh sách tuyến đường từ API
  useEffect(() => {
    const fetchRoutes = async () => {
      try {
        const response = await fetch('http://localhost:8080/PublicTransport/api/routes');
        if (!response.ok) {
          throw new Error('Không thể lấy dữ liệu từ API');
        }
        const data = await response.json();

        const mappedRoutes = data.routes.map(route => ({
          id: route.id,
          number: route.code,
          route: route.name,
          time: '05:00 - 19:00',
          price: '6.000 VNĐ',
        }));

        setBusRoutes(mappedRoutes);
        setRouteVariantsMap(data.routeVariantsMap || {});
        setLoading(false);
      } catch (err) {
        setError(err.message);
        setLoading(false);
      }
    };

    fetchRoutes();
  }, []);

  // Hàm xử lý khi nhấp vào tuyến đường
  const handleRouteClick = async (routeId) => {
    try {
      const response = await fetch(`http://localhost:8080/PublicTransport/api/routes/${routeId}`);
      if (!response.ok) {
        throw new Error('Không thể lấy chi tiết tuyến đường');
      }
      const data = await response.json();
      setSelectedRoute(data.route || null);
      setSelectedVariants(data.variants || []);
      setSelectedVariantStopsMap(data.variantStopsMap || {});
      setVariantSchedulesMap(data.variantSchedulesMap || {});
      setScheduleTripsMap(data.scheduleTripsMap || {});
    } catch (err) {
      setError(err.message);
      setSelectedRoute(null);
      setSelectedVariants([]);
      setSelectedVariantStopsMap({});
      setVariantSchedulesMap({});
      setScheduleTripsMap({});
    }
  };

  // Hàm quay lại danh sách
  const handleBack = () => {
    setSelectedRoute(null);
    setSelectedVariants([]);
    setSelectedVariantStopsMap({});
    setVariantSchedulesMap({});
    setScheduleTripsMap({});
  };

  if (loading) {
    return <div className="flex justify-center items-center h-screen">Đang tải...</div>;
  }

  if (error) {
    return <div className="flex justify-center items-center h-screen text-red-500">Lỗi: {error}</div>;
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 h-screen">
      {selectedRoute ? (
        <RouteDetail
          route={selectedRoute}
          variants={selectedVariants}
          variantStopsMap={selectedVariantStopsMap}
          variantSchedulesMap={variantSchedulesMap}
          scheduleTripsMap={scheduleTripsMap}
          onBack={handleBack}
        />
      ) : (
        <RouteSearch
          busRoutes={busRoutes}
          routeVariantsMap={routeVariantsMap}
          onRouteClick={handleRouteClick}
        />
      )}

      <div className="col-span-2 relative">
        <iframe
          title="Google Map"
          width="100%"
          height="100%"
          loading="lazy"
          style={{ border: 0 }}
          allowFullScreen
          src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3919.494066324302!2d106.6799838758711!3d10.773374659258782!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752f1d63a8adcd%3A0xa51fc27b34c8055!2zQmnDqm4gVGjDoG5oLCBC4buLY2ggTmh1LCBI4buTIENow60gTWluaCwgVmnhu4d0IE5hbQ!5e0!3m2!1svi!2s!4v1685536873232!5m2!1svi!2s"
        ></iframe>

        <div className="absolute top-4 right-4 bg-white p-2 rounded shadow">
          📍 Địa điểm xung quanh
        </div>
      </div>
    </div>
  );
}