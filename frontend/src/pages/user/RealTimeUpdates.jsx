import React, { useState } from "react";

export default function RealTimeUpdates() {
  const [trafficReports, setTrafficReports] = useState([
    { location: "Trạm A", status: "Kẹt xe", image: "https://via.placeholder.com/100" },
    { location: "Trạm B", status: "Sự cố giao thông", image: "https://via.placeholder.com/100" },
  ]);

  // Hàm để báo cáo tình trạng giao thông mới
  const reportTraffic = () => {
    // Ví dụ: Thêm một báo cáo tình trạng giao thông mới
    const newReport = {
      location: "Trạm C",
      status: "Đường tắc",
      image: "https://via.placeholder.com/100",
    };
    setTrafficReports((prevReports) => [...prevReports, newReport]);
  };

  // Hàm để xóa báo cáo tình trạng giao thông
  const removeTrafficReport = (index) => {
    const updatedReports = trafficReports.filter((_, i) => i !== index);
    setTrafficReports(updatedReports);
  };

  return (
    <div className="max-w-md mx-auto bg-white p-6 rounded-xl shadow-md">
      <h2 className="text-2xl font-bold mb-4">Cập nhật thời gian thực</h2>
      
      <div className="space-y-4">
        {trafficReports.map((report, index) => (
          <div key={index} className="flex items-center space-x-4 p-4 border rounded-lg">
            <img src={report.image} alt="traffic report" className="w-16 h-16 rounded-full" />
            <div>
              <p><strong>{report.location}</strong></p>
              <p>{report.status}</p>
              <button
                onClick={() => removeTrafficReport(index)}
                className="mt-2 bg-red-600 text-white py-1 px-4 rounded"
              >
                Xóa báo cáo
              </button>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-4">
        <button
          onClick={reportTraffic}
          className="w-full bg-yellow-600 text-white py-2 rounded"
        >
          Báo cáo tình trạng giao thông
        </button>
      </div>
    </div>
  );
}
