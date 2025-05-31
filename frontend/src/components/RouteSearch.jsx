// export default RouteSearch;
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom"; // Import useNavigate
import { fetchRoutes, setCurrentPage } from "../features/routes/routeSlice";

const RouteSearch = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate(); // Initialize useNavigate

  const { routes, totalItems, totalPages, currentPage, status, error } =
    useSelector((state) => state.busRoutes);

  const [searchParams, setSearchParams] = useState({
    name: "",
    startStop: "",
    endStop: "",
    code: "",
    type: "",
  });

  useEffect(() => {
    dispatch(fetchRoutes({ page: currentPage, size: 10, searchParams }));
  }, [dispatch, currentPage, searchParams]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setSearchParams((prev) => ({ ...prev, [name]: value }));
  };

  const handleReset = () => {
    const resetParams = {
      name: "",
      startStop: "",
      endStop: "",
      code: "",
      type: "",
    };
    setSearchParams(resetParams);
    dispatch(setCurrentPage(1));
  };

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) {
      dispatch(setCurrentPage(page));
    }
  };

  const getTypeIcon = (type) => {
    switch (type?.toUpperCase()) {
      case "BUS":
        return "🚌";
      case "ELECTRIC":
        return "🚆";
      default:
        return "❓";
    }
  };

  // Handle route click to navigate to the route variants page
  const handleRouteClick = (route) => {
    navigate(`/route/${route.id}`, { state: { route } }); // Pass the route data via state
  };

  const renderPagination = () => {
    const pages = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

    if (endPage - startPage + 1 < maxPagesToShow) {
      startPage = Math.max(1, endPage - maxPagesToShow + 1);
    }

    if (startPage > 1) {
      pages.push(
        <button
          key={1}
          onClick={() => handlePageChange(1)}
          className={`px-3 py-1 mx-1 rounded ${
            currentPage === 1
              ? "bg-red-500 text-white"
              : "bg-gray-200 hover:bg-gray-300"
          }`}
        >
          1
        </button>
      );
      if (startPage > 2) {
        pages.push(
          <span key="start-ellipsis" className="px-3 py-1 mx-1">
            ...
          </span>
        );
      }
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(
        <button
          key={i}
          onClick={() => handlePageChange(i)}
          className={`px-3 py-1 mx-1 rounded ${
            currentPage === i
              ? "bg-red-500 text-white"
              : "bg-gray-200 hover:bg-gray-300"
          }`}
        >
          {i}
        </button>
      );
    }

    if (endPage < totalPages) {
      if (endPage < totalPages - 1) {
        pages.push(
          <span key="end-ellipsis" className="px-3 py-1 mx-1">
            ...
          </span>
        );
      }
      pages.push(
        <button
          key={totalPages}
          onClick={() => handlePageChange(totalPages)}
          className={`px-3 py-1 mx-1 rounded ${
            currentPage === totalPages
              ? "bg-red-500 text-white"
              : "bg-gray-200 hover:bg-gray-300"
          }`}
        >
          {totalPages}
        </button>
      );
    }

    return (
      <div className="flex items-center justify-center mt-4">
        <button
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage === 1}
          className="px-3 py-1 mx-1 rounded bg-gray-200 hover:bg-gray-300 disabled:opacity-50"
        >
          ←
        </button>
        {pages}
        <button
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={currentPage === totalPages}
          className="px-3 py-1 mx-1 rounded bg-gray-200 hover:bg-gray-300 disabled:opacity-50"
        >
          →
        </button>
      </div>
    );
  };

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Danh sách tuyến xe</h1>

      {/* Form tìm kiếm */}
      <div className="mb-6 p-4 border rounded-lg shadow bg-gray-50">
        <h2 className="text-lg font-semibold mb-3">Tìm kiếm tuyến xe</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium mb-1">Tên tuyến</label>
            <input
              type="text"
              name="name"
              value={searchParams.name}
              onChange={handleInputChange}
              className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Nhập tên tuyến..."
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Điểm đi</label>
            <input
              type="text"
              name="startStop"
              value={searchParams.startStop}
              onChange={handleInputChange}
              className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Nhập điểm đi..."
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Điểm đến</label>
            <input
              type="text"
              name="endStop"
              value={searchParams.endStop}
              onChange={handleInputChange}
              className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Nhập điểm đến..."
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Mã tuyến</label>
            <input
              type="text"
              name="code"
              value={searchParams.code}
              onChange={handleInputChange}
              className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Nhập mã tuyến..."
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Loại tuyến</label>
            <select
              name="type"
              value={searchParams.type}
              onChange={handleInputChange}
              className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Chọn loại tuyến</option>
              <option value="BUS">Bus</option>
              <option value="ELECTRIC">Electric</option>
            </select>
          </div>
        </div>
        <div className="mt-4 flex justify-end gap-3">
          <button
            onClick={handleReset}
            className="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
          >
            Đặt lại
          </button>
        </div>
      </div>

      {status === "loading" && <p>Đang tải dữ liệu...</p>}
      {status === "failed" && <p className="text-red-500">Lỗi: {error}</p>}
      {status === "succeeded" && (
        <>
          <div className="max-h-[500px] overflow-y-auto space-y-2">
            <ul>
              {routes.length === 0 ? (
                <p>Không có tuyến xe nào.</p>
              ) : (
                routes.map((route) => (
                  <li
                    key={route.id}
                    className="p-4 border rounded-lg shadow hover:bg-gray-100 transition cursor-pointer"
                    onClick={() => handleRouteClick(route)}
                  >
                    <div className="flex items-center gap-3">
                      <span className="text-2xl">
                        {getTypeIcon(route.type)}
                      </span>
                      <div>
                        <p>
                          <strong>Mã tuyến:</strong> {route.code}
                        </p>
                        <p>
                          <strong>Tên tuyến:</strong> {route.name}
                        </p>
                        <p>
                          <strong>Loại tuyến:</strong> {route.type}
                        </p>
                      </div>
                    </div>
                  </li>
                ))
              )}
            </ul>
          </div>
          {totalPages > 1 && renderPagination()}
        </>
      )}

      <div className="mt-4 text-sm text-gray-500">
        Tổng số: {totalItems} | Trang: {currentPage}/{totalPages}
      </div>
    </div>
  );
};

export default RouteSearch;
