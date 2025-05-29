import React from 'react';

const StopsList = ({ stops }) => {
  if (!stops || stops.length === 0) {
    return <p>Không có thông tin trạm dừng.</p>;
  }

  return (
    <div className="space-y-2">
      {stops.map((stop) => (
        <div key={stop.id} className="p-4 border rounded-lg shadow bg-gray-50">
          <p><strong>{stop.station.name}</strong></p>
          <p>
            {stop.station.location.address}, {stop.station.location.street},{' '}
            {stop.station.location.ward ? `${stop.station.location.ward}, ` : ''}
            {stop.station.location.zone}
          </p>
          <p><strong>Thứ tự:</strong> {stop.stopOrder}</p>
        </div>
      ))}
    </div>
  );
};

export default StopsList;
