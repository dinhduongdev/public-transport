
import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const RouteVariants = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { route } = location.state || {}; // Get the route data from navigation state
  console.log(route);
  

  // State to track the active tab ("outbound" for Lượt đi, "return" for Lượt về)
  const [activeTab, setActiveTab] = useState('outbound');
  // State to track the active sub-tab ("schedule", "stops", "info", "reviews")
  const [activeSubTab, setActiveSubTab] = useState('schedule');
  // State to store the fetched schedule data
  const [scheduleData, setScheduleData] = useState(null);
  // State to handle loading and error states for the API call
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);


  const { routeVariants } = route;
  const outboundVariant = routeVariants.find((v) => v.isOutbound); // Lượt đi
  const returnVariant = routeVariants.find((v) => !v.isOutbound); // Lượt về

  // Fetch schedule data when the active tab changes
  useEffect(() => {
    const fetchSchedule = async (variantId) => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(`http://localhost:8080/PublicTransport/api/route-variants/${variantId}`);
        if (!response.ok) {
          throw new Error('Không thể tải dữ liệu lịch trình.');
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

    const variantId = activeTab === 'outbound' ? outboundVariant.id : returnVariant.id;
    fetchSchedule(variantId);
  }, [activeTab, outboundVariant, returnVariant]);

  // Handle tab switching
  const handleTabChange = (tab) => {
    setActiveTab(tab);
  };

  // Handle sub-tab switching
  const handleSubTabChange = (subTab) => {
    setActiveSubTab(subTab);
  };

  // Render the schedule in a grid format (6 columns) and highlight the next trip
  const renderSchedule = () => {
    if (loading) {
      return <p>Đang tải dữ liệu...</p>;
    }
    if (error) {
      return <p className="text-red-500">Lỗi: {error}</p>;
    }
    if (!scheduleData || !scheduleData.scheduleTripsMap || !scheduleData.scheduleTripsMap[activeTab === 'outbound' ? outboundVariant.id : returnVariant.id]) {
      return <p>Không có lịch trình nào.</p>;
    }

    const trips = scheduleData.scheduleTripsMap[activeTab === 'outbound' ? outboundVariant.id : returnVariant.id];
    const times = trips.map((trip) => {
      const [hour, minute] = trip.startTime;
      return `${hour}:${minute < 10 ? `0${minute}` : minute}`; // Format as "H:MM"
    });

    // Find the next trip after the current time (17:30 on May 28, 2025)
    const currentTime = new Date('2025-05-28T17:30:00+07:00');
    const currentHours = currentTime.getHours();
    const currentMinutes = currentTime.getMinutes();
    const currentTimeInMinutes = currentHours * 60 + currentMinutes;

    let nextTripIndex = -1;
    for (let i = 0; i < trips.length; i++) {
      const [hour, minute] = trips[i].startTime;
      const tripTimeInMinutes = hour * 60 + minute;
      if (tripTimeInMinutes >= currentTimeInMinutes) {
        nextTripIndex = i;
        break;
      }
    }

    // Group times into rows of 6
    const rows = [];
    for (let i = 0; i < times.length; i += 6) {
      rows.push(times.slice(i, i + 6));
    }

    return (
      <div className="grid grid-cols-6 gap-2">
        {rows.map((row, rowIndex) =>
          row.map((time, colIndex) => {
            const flatIndex = rowIndex * 6 + colIndex;
            const isNextTrip = flatIndex === nextTripIndex;
            return (
              <div
                key={`${rowIndex}-${colIndex}`}
                className={`text-center p-2 rounded ${
                  isNextTrip ? 'bg-yellow-200 font-bold' : ''
                }`}
              >
                {time}
              </div>
            );
          })
        )}
      </div>
    );
  };

  // Render the list of stops
  const renderStops = () => {
    if (!scheduleData || !scheduleData.stops || scheduleData.stops.length === 0) {
      return <p>Không có thông tin trạm dừng.</p>;
    }

    return (
      <div className="space-y-2">
        {scheduleData.stops.map((stop) => (
          <div
            key={stop.id}
            className="p-4 border rounded-lg shadow bg-gray-50"
          >
            <p><strong>{stop.station.name}</strong></p>
            <p>
              {stop.station.location.address}, {stop.station.location.street},{' '}
              {stop.station.location.ward ? `${stop.station.location.ward}, ` : ''}{stop.station.location.zone}
            </p>
            <p><strong>Thứ tự:</strong> {stop.stopOrder}</p>
          </div>
        ))}
      </div>
    );
  };

  // Render the info section
  const renderInfo = () => {
    if (!scheduleData || !scheduleData.routeVariant) {
      return <p>Không có thông tin tuyến xe.</p>;
    }

    const { routeVariant } = scheduleData;
    return (
      <div className="p-4 border rounded-lg shadow bg-gray-50">
        <p><strong>Tên tuyến:</strong> {route.name}</p>
        <p><strong>Mã tuyến:</strong> {route.code}</p>
        <p><strong>Loại tuyến:</strong> {route.type}</p>
        <p><strong>Điểm đi:</strong> {routeVariant.startStop}</p>
        <p><strong>Điểm đến:</strong> {routeVariant.endStop}</p>
        <p><strong>Khoảng cách:</strong> {(routeVariant.distance / 1000).toFixed(2)} km</p>
      </div>
    );
  };

  // Render the reviews section (placeholder)
  const renderReviews = () => {
    return (
      <div className="p-4 border rounded-lg shadow bg-gray-50">
        <p>Chưa có đánh giá nào cho tuyến xe này.</p>
        <p>Bạn có thể thêm đánh giá tại đây (chức năng chưa được triển khai).</p>
      </div>
    );
  };

  // Render the content based on the active sub-tab
  const renderContent = () => {
    switch (activeSubTab) {
      case 'schedule':
        return (
          <div>
            <h3 className="text-lg font-semibold mb-2">Hôm nay</h3>
            {renderSchedule()}
          </div>
        );
      case 'stops':
        return (
          <div>
            <h3 className="text-lg font-semibold mb-2">Danh sách trạm dừng</h3>
            {renderStops()}
          </div>
        );
      case 'info':
        return (
          <div>
            <h3 className="text-lg font-semibold mb-2">Thông tin tuyến xe</h3>
            {renderInfo()}
          </div>
        );
      case 'reviews':
        return (
          <div>
            <h3 className="text-lg font-semibold mb-2">Đánh giá</h3>
            {renderReviews()}
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="p-4">
      <div className="flex items-center mb-4">
        <button
          onClick={() => navigate('/')} // Navigate back to the search page
          className="mr-2 text-gray-600 hover:text-gray-800"
        >
          ←
        </button>
        <h1 className="text-2xl font-bold">Tuyến số {route.code}</h1>
      </div>

      <h2 className="text-lg font-semibold mb-3">{route.name}</h2>

      {/* Tabs for Lượt đi and Lượt về */}
      <div className="mb-4 flex gap-4">
        <button
          onClick={() => handleTabChange('outbound')}
          className={`px-4 py-2 rounded ${
            activeTab === 'outbound'
              ? 'bg-teal-600 text-white'
              : 'bg-gray-200 text-gray-700'
          }`}
        >
          Lượt đi
        </button>
        <button
          onClick={() => handleTabChange('return')}
          className={`px-4 py-2 rounded ${
            activeTab === 'return'
              ? 'bg-teal-600 text-white'
              : 'bg-gray-200 text-gray-700'
          }`}
        >
          Lượt về
        </button>
      </div>

      {/* Route Variant Details */}
      <div className="mb-4 p-4 border rounded-lg shadow bg-gray-50">
        <h3 className="text-lg font-semibold">
          {activeTab === 'outbound' ? 'Lượt đi' : 'Lượt về'}
        </h3>
        <p><strong>Điểm đi:</strong> {activeTab === 'outbound' ? outboundVariant.startStop : returnVariant.startStop}</p>
        <p><strong>Điểm đến:</strong> {activeTab === 'outbound' ? outboundVariant.endStop : returnVariant.endStop}</p>
        <p><strong>Khoảng cách:</strong> {((activeTab === 'outbound' ? outboundVariant.distance : returnVariant.distance) / 1000).toFixed(2)} km</p>
      </div> 

      {/* Sub-tabs for Biểu đồ giờ, Trạm dừng, Thông tin, Đánh giá */}
      <div className="mb-4 flex gap-2">
        <button
          onClick={() => handleSubTabChange('schedule')}
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
          onClick={() => handleSubTabChange('stops')}
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
          onClick={() => handleSubTabChange('info')}
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
          onClick={() => handleSubTabChange('reviews')}
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

      {/* Content based on active sub-tab */}
      {renderContent()}
    </div>
  );
};

export default RouteVariants;