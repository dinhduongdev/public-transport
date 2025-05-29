import React from 'react';
import { useSelector } from 'react-redux';

const Schedule = ({ variantId, loading, error }) => {
  const scheduleData = useSelector((state) => state.routeVariants.schedules[variantId]);

  if (loading) {
    return <p>Đang tải dữ liệu...</p>;
  }
  if (error) {
    return <p className="text-red-500">Lỗi: {error}</p>;
  }
  if (!scheduleData || !scheduleData.scheduleTripsMap || !scheduleData.scheduleTripsMap[variantId]) {
    return <p>Không có lịch trình nào.</p>;
  }

  const trips = scheduleData.scheduleTripsMap[variantId];
  const times = trips.map((trip) => {
    const [hour, minute] = trip.startTime;
    return `${hour}:${minute < 10 ? `0${minute}` : minute}`;
  });

  // Tìm chuyến tiếp theo sau thời gian hiện tại (17:35 ngày 28/05/2025)
  const currentTime = new Date('2025-05-28T17:35:00+07:00');
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

  // Nhóm thời gian thành các hàng 6 cột
  const rows = [];
  for (let i = 0; i < times.length; i += 6) {
    rows.push(times.slice(i, i + 6));
  }

  return (
    <div>
      <h3 className="text-lg font-semibold mb-2">Hôm nay</h3>
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
    </div>
  );
};

export default Schedule;