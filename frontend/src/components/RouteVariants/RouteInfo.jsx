import React from 'react';

const RouteInfo = ({ routeVariant }) => {
  if (!routeVariant) return <p>Không có thông tin tuyến xe.</p>;

  const { name, startStop, endStop, distance, isOutbound } = routeVariant;

  return (
    <div className="p-4 border rounded-lg shadow bg-gray-50 space-y-2">
      <h2 className="text-xl font-semibold text-blue-700">{name}</h2>
      <p><span className="font-medium">Chiều:</span> {isOutbound ? 'Lượt đi' : 'Lượt về'}</p>
      <p><span className="font-medium">Điểm đầu:</span> {startStop}</p>
      <p><span className="font-medium">Điểm cuối:</span> {endStop}</p>
      <p><span className="font-medium">Khoảng cách:</span> {distance.toLocaleString()} mét</p>
    </div>
  );
};

export default RouteInfo;
