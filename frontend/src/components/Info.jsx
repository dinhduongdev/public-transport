import React from 'react';
import { useSelector } from 'react-redux';

const Info = ({ route }) => {
  const activeTab = useSelector((state) => state.routeVariants.activeTab);
  const variantId = activeTab === 'outbound'
    ? route.routeVariants.find((v) => v.isOutbound)?.id
    : route.routeVariants.find((v) => !v.isOutbound)?.id;
  const scheduleData = useSelector((state) => state.routeVariants.schedules[variantId]);

  if (!scheduleData || !scheduleData.routeVariant) {
    return <p>Không có thông tin tuyến xe.</p>;
  }

  const { routeVariant } = scheduleData;
  return (
    <div>
      <h3 className="text-lg font-semibold mb-2">Thông tin tuyến xe</h3>
      <div className="p-4 border rounded-lg shadow bg-gray-50">
        <p><strong>Tên tuyến:</strong> {route.name}</p>
        <p><strong>Mã tuyến:</strong> {route.code}</p>
        <p><strong>Loại tuyến:</strong> {route.type}</p>
        <p><strong>Điểm đi:</strong> {routeVariant.startStop}</p>
        <p><strong>Điểm đến:</strong> {routeVariant.endStop}</p>
        <p><strong>Khoảng cách:</strong> {(routeVariant.distance / 1000).toFixed(2)} km</p>
      </div>
    </div>
  );
};

export default Info;