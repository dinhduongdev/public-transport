// // import React from 'react';

// // const RouteSchedule = ({
// //   scheduleData,
// //   loading,
// //   error,
// //   activeTab,
// //   outboundVariant,
// //   returnVariant,
// // }) => {
// //     console.log(scheduleData);
    



// //   if (loading) {
// //     return <p>Đang tải dữ liệu lịch trình...</p>;
// //   }

// //   if (error) {
// //     return <p className="text-red-500">Lỗi: {error}</p>;
// //   }

// //   if (!scheduleData || !scheduleData.trips || scheduleData.trips.length === 0) {
// //     return <p>Không có chuyến xe nào cho lượt này.</p>;
// //   }


// //   // Hiển thị giờ khởi hành của các chuyến từ điểm đầu
// //   const tripTimes = scheduleData.trips.map(trip => trip.departureTime);

// //   return (
// //     <div>
// //       <p>
// //         <strong>Lượt {activeTab === 'outbound' ? 'đi' : 'về'}:</strong>{' '}
// //         từ {activeTab === 'outbound' ? outboundVariant.startStop : returnVariant.startStop}{' '}
// //         đến {activeTab === 'outbound' ? outboundVariant.endStop : returnVariant.endStop}
// //       </p>
// //       <div className="mt-2 grid grid-cols-4 sm:grid-cols-6 md:grid-cols-8 lg:grid-cols-10 gap-2">
// //         {tripTimes.map((time, index) => (
// //           <div key={index} className="bg-teal-100 text-teal-800 px-2 py-1 rounded text-sm text-center">
// //             {time}
// //           </div>
// //         ))}
// //       </div>
// //     </div>
// //   );
// // };

// // export default RouteSchedule;


// import React from 'react';

// const RouteSchedule = ({
//   scheduleData,
//   loading,
//   error,
//   activeTab,
//   outboundVariant,
//   returnVariant,
// }) => {
//     console.log('====================================');
//     console.log("scheduleData",scheduleData);
//     console.log('====================================');


//   if (loading) {
//     return <p>Đang tải dữ liệu lịch trình...</p>;
//   }

//   if (error) {
//     return <p className="text-red-500">Lỗi: {error}</p>;
//   }

//   // Check if scheduleData and scheduleTripsMap exist
//   if (
//     !scheduleData ||
//     !scheduleData.scheduleTripsMap ||
//     !scheduleData.scheduleTripsMap['1'] ||
//     scheduleData.scheduleTripsMap['1'].length === 0
//   ) {
//     return <p>Không có chuyến xe nào cho lượt này.</p>;
//   }

//   // Extract trips from scheduleTripsMap["1"]
//   const trips = scheduleData.scheduleTripsMap['1'];

//   // Format startTime [hour, minute] into "HH:MM" string
//   const tripTimes = trips.map(trip => {
//     const [hour, minute] = trip.startTime;
//     return `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
//   });

//   return (
//     <div>
//       <p>
//         <strong>Lượt {activeTab === 'outbound' ? 'đi' : 'về'}:</strong>{' '}
//         từ {activeTab === 'outbound' ? outboundVariant.startStop : returnVariant.startStop}{' '}
//         đến {activeTab === 'outbound' ? outboundVariant.endStop : returnVariant.endStop}
//       </p>
//       <div className="mt-2 grid grid-cols-4 sm:grid-cols-6 md:grid-cols-8 lg:grid-cols-10 gap-2">
//         {tripTimes.map((time, index) => (
//           <div key={index} className="bg-teal-100 text-teal-800 px-2 py-1 rounded text-sm text-center">
//             {time}
//           </div>
//         ))}
//       </div>
//     </div>
//   );
// };

// export default RouteSchedule;

import React from 'react';

const RouteSchedule = ({
  scheduleData,
  loading,
  error,
  activeTab,
  outboundVariant,
  returnVariant,
}) => {
  console.log('====================================');
  console.log("scheduleData", scheduleData);
  console.log('====================================');

  if (loading) {
    return <p>Đang tải dữ liệu lịch trình...</p>;
  }

  if (error) {
    return <p className="text-red-500">Lỗi: {error}</p>;
  }

  const scheduleMap = scheduleData?.scheduleTripsMap;
  const firstKey = scheduleMap ? Object.keys(scheduleMap)[0] : null;
  const trips = firstKey ? scheduleMap[firstKey] : [];

  if (!trips || trips.length === 0) {
    return <p>Không có chuyến xe nào cho lượt này.</p>;
  }

  const tripTimes = trips.map((trip) => {
    const [hour, minute] = trip.startTime;
    return `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
  });

  return (
    <div>
      <p>
        <strong>Lượt {activeTab === 'outbound' ? 'đi' : 'về'}:</strong>{' '}
        từ {activeTab === 'outbound' ? outboundVariant.startStop : returnVariant.startStop}{' '}
        đến {activeTab === 'outbound' ? outboundVariant.endStop : returnVariant.endStop}
      </p>
      <div className="mt-2 grid grid-cols-4 sm:grid-cols-6 md:grid-cols-8 lg:grid-cols-10 gap-2">
        {tripTimes.map((time, index) => (
          <div key={index} className="bg-teal-100 text-teal-800 px-2 py-1 rounded text-sm text-center">
            {time}
          </div>
        ))}
      </div>
    </div>
  );
};

export default RouteSchedule;
