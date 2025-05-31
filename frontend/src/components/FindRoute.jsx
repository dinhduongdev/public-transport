import React, { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { MapPin, RefreshCw } from "lucide-react";
import {
  fetchRouteNavigation,
  clearNavigation,
  selectStop,
  setSelectedRouteIndex,
} from "../features/routes/navigationSlice";

export default function FindRoute() {
  const [startPoint, setStartPoint] = useState("");
  const [endPoint, setEndPoint] = useState("");
  const [maxNumOfTrip, setMaxNumOfTrip] = useState(1);

  const dispatch = useDispatch();
  const { routes, loading, error, selectedRouteIndex } = useSelector(
    (state) => state.navigation
  );

  const handleSwap = () => {
    setStartPoint(endPoint);
    setEndPoint(startPoint);
  };

  const handleSearch = () => {
    if (startPoint && endPoint) {
      dispatch(
        fetchRouteNavigation({
          startKw: startPoint,
          endKw: endPoint,
          maxNumOfTrip,
        })
      );
    }
  };

  const handleClear = () => {
    setStartPoint("");
    setEndPoint("");
    dispatch(clearNavigation());
  };

  const handleStopClick = (coordinates) => {
    dispatch(selectStop(coordinates));
  };

  const handleRouteChange = (e) => {
    const index = Number(e.target.value);
    dispatch(setSelectedRouteIndex(index)); // Dispatch tuyến được chọn
  };

  return (
    <div className="max-w-3xl mx-auto p-6 space-y-6 bg-gray-50 min-h-screen">
      {/* Input Card */}
      <div className="bg-white p-6 rounded-xl shadow-lg space-y-4">
        <h2 className="text-xl font-bold text-gray-800">Tìm Tuyến Đường</h2>
        <div className="space-y-3">
          <div className="flex items-center gap-3">
            <MapPin className="text-emerald-500 flex-shrink-0" size={20} />
            <input
              type="text"
              placeholder="Điểm bắt đầu (e.g., 1303 Lê Văn Lương)"
              value={startPoint}
              onChange={(e) => setStartPoint(e.target.value)}
              className="w-full p-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-emerald-400 bg-gray-50 text-gray-700 placeholder-gray-400"
              aria-label="Điểm bắt đầu"
            />
          </div>
          <div className="flex items-center gap-3">
            <MapPin className="text-rose-500 flex-shrink-0" size={20} />
            <input
              type="text"
              placeholder="Điểm đến (e.g., Huỳnh Tấn Phát)"
              value={endPoint}
              onChange={(e) => setEndPoint(e.target.value)}
              className="w-full p-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-rose-400 bg-gray-50 text-gray-700 placeholder-gray-400"
              aria-label="Điểm đến"
            />
          </div>
        </div>
        <div className="flex justify-between items-center">
          <button
            onClick={handleSwap}
            className="p-2 bg-gray-100 rounded-full hover:bg-gray-200 transition-colors"
            title="Đổi điểm"
            aria-label="Đổi điểm bắt đầu và điểm đến"
          >
            <RefreshCw className="text-gray-600" size={18} />
          </button>
          <button
            onClick={handleSearch}
            disabled={!startPoint || !endPoint}
            className="px-6 py-2 bg-teal-600 text-white rounded-lg disabled:bg-gray-300 disabled:cursor-not-allowed hover:bg-teal-700 transition-colors font-medium"
            aria-label="Tìm tuyến đường"
          >
            Tìm Tuyến
          </button>
        </div>
      </div>

      {/* Filters Card */}
      <div className="bg-white p-6 rounded-xl shadow-lg">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-center gap-3">
            <span className="text-sm font-semibold text-gray-700">
              Chỉ Tuyến Hoạt Động
            </span>
            {/* Add toggle logic if needed */}
          </div>
          <div className="flex gap-2">
            {[1, 2, 3].map((n) => (
              <button
                key={n}
                onClick={() => setMaxNumOfTrip(n)}
                className={`px-4 py-1.5 rounded-lg border text-sm font-medium ${
                  maxNumOfTrip === n
                    ? "bg-teal-600 text-white border-teal-600"
                    : "bg-gray-100 text-gray-700 border-gray-200 hover:bg-gray-200"
                } transition-colors`}
                aria-label={`Chọn ${n} tuyến`}
              >
                {n} Tuyến
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Results Section */}
      <div className="space-y-4">
        {loading && (
          <div className="text-center py-4">
            <div className="inline-block animate-spin rounded-full h-6 w-6 border-t-2 border-b-2 border-teal-500"></div>
            <p className="text-gray-600 mt-2">Đang tải tuyến đường...</p>
          </div>
        )}
        {error && (
          <div className="bg-rose-50 border border-rose-200 text-rose-600 p-4 rounded-lg text-center">
            Lỗi: {error}
          </div>
        )}
        {!loading && routes.length === 0 && !error && (
          <div className="bg-gray-100 p-4 rounded-lg text-center text-gray-500">
            Nhập điểm bắt đầu và điểm đến để tìm tuyến đường.
          </div>
        )}
        {!loading && routes.length > 0 && (
          <div className="bg-white p-6 rounded-xl shadow-lg space-y-4">
            <div className="flex items-center gap-2">
              <span className="text-lg font-semibold text-gray-800">
                Có {routes.length} cách để đi
              </span>
              <select
                value={selectedRouteIndex}
                onChange={handleRouteChange}
                className="p-2 border border-gray-200 rounded-lg bg-gray-50 text-gray-700 focus:outline-none focus:ring-2 focus:ring-teal-400"
                aria-label="Chọn tuyến đường"
              >
                {routes.map((_, index) => (
                  <option key={index} value={index}>
                    Cách {index + 1}
                  </option>
                ))}
              </select>
            </div>
            {routes[selectedRouteIndex] && (
              <div className="space-y-3">
                <div className="flex justify-between items-start">
                  <span className="text-xs bg-teal-100 text-teal-700 px-2 py-1 rounded-full">
                    Cách {selectedRouteIndex + 1}
                  </span>
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm text-gray-600">
                  <p>
                    <span className="font-medium">Bắt đầu:</span> (
                    {routes[selectedRouteIndex].formattedStartAddress})
                  </p>
                  <p>
                    <span className="font-medium">Kết thúc:</span> (
                    {routes[selectedRouteIndex].formattedEndAddress})
                  </p>
                  <p>
                    <span className="font-medium">Tổng khoảng cách:</span>{" "}
                    {(
                      routes[selectedRouteIndex].totalDistanceInMeters / 1000
                    ).toFixed(2)}{" "}
                    km
                  </p>
                </div>
                <h4 className="font-medium text-gray-700">Các Trạm:</h4>
                <ul className="space-y-2 text-sm text-gray-600 max-h-60 overflow-y-auto">
                  {" "}
                  {/* Thêm max-h-60 và overflow-y-auto */}
                  {routes[selectedRouteIndex].hops.map((hop) => (
                    <li
                      key={hop.stop.id}
                      className="flex items-start gap-2 cursor-pointer hover:bg-gray-100 p-2 rounded transition-colors"
                      onClick={() =>
                        handleStopClick(hop.stop.station.coordinates)
                      }
                    >
                      <span className="text-teal-500">•</span>
                      <div>
                        {(hop.order === 1 ||
                          hop.route.id !==
                            routes[selectedRouteIndex].hops[hop.order - 2]
                              ?.route.id) && (
                          <div className="text-sm text-teal-600 font-medium mb-1">
                            Tuyến: {hop.route.name} ,Mã tuyến: ({hop.route.code})
                          </div>
                        )}
                        <span className="font-medium">
                          {hop.stop.station.name}
                        </span>{" "}
                        - {hop.stop.station.location.address},{" "}
                        {hop.stop.station.location.street},{" "}
                        {hop.stop.station.location.ward || "N/A"},{" "}
                        {hop.stop.station.location.zone}
                        {hop.distanceToNextStop > 0 && (
                          <span className="text-gray-500">
                            {" "}
                            (Tiếp theo: {hop.distanceToNextStop.toFixed(2)}m)
                          </span>
                        )}
                      </div>
                    </li>
                  ))}
                </ul>
                <p className="text-sm text-gray-600">
                  <span className="font-medium">Thời gian dự kiến:</span>{" "}
                  {routes[selectedRouteIndex].duration > 0
                    ? `${routes[selectedRouteIndex].duration} phút`
                    : "Không có thông tin"}
                </p>
              </div>
            )}
          </div>
        )}

        {routes.length > 0 && (
          <div className="text-center">
            <button
              onClick={handleClear}
              className="px-6 py-2 bg-rose-500 text-white rounded-lg hover:bg-rose-600 transition-colors font-medium"
              aria-label="Xóa kết quả tìm kiếm"
            >
              Xóa Kết Quả
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
