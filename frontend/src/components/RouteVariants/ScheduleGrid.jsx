import React from 'react';

const ScheduleGrid = ({ trips }) => {
  const times = trips.map(({ startTime }) => {
    const [hour, minute] = startTime;
    return `${hour}:${minute < 10 ? `0${minute}` : minute}`;
  });

  const currentTime = new Date('2025-05-28T17:30:00+07:00');
  const currentMinutes = currentTime.getHours() * 60 + currentTime.getMinutes();

  const nextTripIndex = trips.findIndex(([hour, minute]) =>
    hour * 60 + minute >= currentMinutes
  );

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
              className={`text-center p-2 rounded ${isNextTrip ? 'bg-yellow-200 font-bold' : ''}`}
            >
              {time}
            </div>
          );
        })
      )}
    </div>
  );
};

export default ScheduleGrid;
