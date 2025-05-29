import React from 'react';

const RouteHeader = ({ route, onBack }) => (
  <div className="flex items-center mb-4">
    <button
      onClick={onBack}
      className="mr-2 text-gray-600 hover:text-gray-800 text-2xl"
      aria-label="Quay lại"
    >
      ←
    </button>
    <div>
      <h1 className="text-2xl font-bold">Tuyến số {route?.code}</h1>
      <h2 className="text-lg font-semibold text-gray-700">{route?.name}</h2>
    </div>
  </div>
);

export default RouteHeader;