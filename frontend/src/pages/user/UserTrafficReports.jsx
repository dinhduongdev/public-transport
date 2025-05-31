import React, { useEffect } from "react";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { fetchTrafficReports } from "../../features/trafficreport/trafficReportsSlice";
import { formatCreatedAt } from "../../utils/formatCreatedAt";

const UserTrafficReports = () => {
  const dispatch = useDispatch();
  const { reports, loading, error } = useSelector(
    (state) => state.trafficReports
  );
  console.log('====================================');
  console.log(reports);
  console.log('====================================');
  // Lấy danh sách báo cáo của người dùng
  useEffect(() => {
    dispatch(fetchTrafficReports());
  }, [dispatch]);

    // Chuyển đổi mảng thời gian thành đối tượng Date
    const parseTimeArray = (timeArray) => {
      if (!timeArray || !Array.isArray(timeArray) || timeArray.length < 5) {
        return null;
      }
      const [year, month, day, hour, minute] = timeArray;
      // JavaScript months are 0-based, so subtract 1 from month
      return new Date(year, month - 1, day, hour, minute);
    };
  return (
    <div className="container mx-auto mt-8 px-4">
      {/* Nút Quay lại */}
      <button
        onClick={() => window.history.back()}
        className="mb-4 inline-flex items-center px-3 py-2 text-sm font-medium text-white bg-gray-600 rounded-lg hover:bg-gray-700"
      >
        Quay lại
      </button>

      <h1 className="text-2xl font-bold mb-6">Báo cáo giao thông của bạn</h1>

      {/* Nút Tạo báo cáo */}
      <Link
        to="/create-report"
        className="mb-6 inline-block px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700"
      >
        Tạo báo cáo
      </Link>

      {/* Thông báo trạng thái */}
      {loading && (
        <div className="mb-4 p-4 text-blue-700 bg-blue-100 rounded-lg">
          Đang tải danh sách báo cáo...
        </div>
      )}
      {error && (
        <div className="mb-4 p-4 text-red-700 bg-red-100 rounded-lg">
          {error || "Không thể tải danh sách báo cáo. Vui lòng thử lại."}
        </div>
      )}

      {/* Bảng danh sách báo cáo */}
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-200 rounded-lg shadow-md">
          <thead className="bg-gray-800 text-white">
            <tr>
              <th className="py-3 px-4 text-left text-sm font-medium">ID</th>
              <th className="py-3 px-4 text-left text-sm font-medium">
                Vị trí
              </th>
              <th className="py-3 px-4 text-left text-sm font-medium">Mô tả</th>
              <th className="py-3 px-4 text-left text-sm font-medium">
                Trạng thái
              </th>
              <th className="py-3 px-4 text-left text-sm font-medium">
                Hình ảnh
              </th>
              <th className="py-3 px-4 text-left text-sm font-medium">
                Thời gian bắt đầu
              </th>
              <th className="py-3 px-4 text-left text-sm font-medium">
                Thời gian kết thúc
              </th>
            </tr>
          </thead>
          <tbody>
            {reports.length > 0 ? (
              reports.map((report) => (
                
                <tr key={report.id} className="border-b hover:bg-gray-50">
                  <td className="py-3 px-4 text-sm">{report.id}</td>
                  <td className="py-3 px-4 text-sm">{report.location}</td>
                  <td className="py-3 px-4 text-sm">{report.description}</td>
                  <td className="py-3 px-4 text-sm">{report.status}</td>
                  <td className="py-3 px-4 text-sm">
                    {report.imageUrl ? (
                      <a
                        href={report.imageUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                      >
                        <img
                          src={report.imageUrl}
                          alt="Hình ảnh"
                          className="max-w-[100px] rounded"
                        />
                      </a>
                    ) : (
                      "Không có"
                    )}
                  </td>
                  <td className="py-3 px-4 text-sm">
                    {parseTimeArray(report.startTime).toString()}
                  </td>
                  <td className="py-3 px-4 text-sm">
                    {parseTimeArray(report.endTime).toString()}
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="6" className="py-4 text-center text-gray-500">
                  Bạn chưa có báo cáo nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default UserTrafficReports;
