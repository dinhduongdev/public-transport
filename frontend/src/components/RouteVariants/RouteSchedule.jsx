

import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  addFavorite,
  fetchFavorites,
} from "../../features/favorites/favoritesSlice";
import { toast } from "react-toastify";

const RouteSchedule = ({
  scheduleData,
  loading,
  error,
  activeTab,
  outboundVariant,
  returnVariant,
}) => {
  const dispatch = useDispatch();
  const {
    favorites,
    loading: favoritesLoading,
    error: favoritesError,
  } = useSelector((state) => state.favorites);
  const user = useSelector((state) => state.user);


  // Lấy scheduleId từ schedules[0].id
  const scheduleId = scheduleData?.schedules?.[0]?.id;
  const isFavorite = favorites.some(
    (fav) => fav.targetId === scheduleId && fav.targetType === "SCHEDULE"
  );

  console.log("====================================");
  console.log(user);
  console.log("scheduleData", scheduleData);
  console.log("scheduleId", scheduleId);
  console.log("favorites", favorites);
  console.log("isFavorite", isFavorite);
  console.log("====================================");

  // Fetch danh sách favorites khi component mount
  useEffect(() => {
    if (user && scheduleId) {
      dispatch(fetchFavorites({ userId:user.id, targetType: "SCHEDULE" }));
    }
  }, [dispatch, user, scheduleId]);

  const handleAddFavorite = async () => {
    if (!scheduleId) {
      toast.error("Không tìm thấy ID lịch trình");
      return;
    }

    try {
      await dispatch(
        addFavorite({
          userId:user.id,
          targetId: scheduleId,
          targetType: "SCHEDULE",
        })
      ).unwrap();
      toast.success("Thêm lịch trình yêu thích thành công");
      // Refetch favorites để cập nhật danh sách
      dispatch(fetchFavorites({ userId:user.id, targetType: "SCHEDULE" }));
    } catch (err) {
      console.error("Failed to add favorite:", err);
      toast.error(err || "Không thể thêm lịch trình yêu thích");
    }
  };

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
    return `${hour.toString().padStart(2, "0")}:${minute
      .toString()
      .padStart(2, "0")}`;
  });

  return (
    <div>
      <button
        onClick={handleAddFavorite}
        disabled={favoritesLoading || isFavorite}
        className={`mt-2 px-4 py-2 text-white border rounded transition ${
          isFavorite
            ? "bg-gray-400 border-gray-500 cursor-not-allowed"
            : "bg-green-600 border-green-700 hover:bg-green-700 hover:border-green-800"
        }`}
      >
        {favoritesLoading
          ? "Đang xử lý..."
          : isFavorite
          ? "Đã yêu thích"
          : "Thêm lịch trình yêu thích"}
      </button>

      {favoritesError && <p className="text-red-500 mt-2">{favoritesError}</p>}

      <p>
        <strong>Lượt {activeTab === "outbound" ? "đi" : "về"}:</strong> từ{" "}
        {activeTab === "outbound"
          ? outboundVariant.startStop
          : returnVariant.startStop}{" "}
        đến{" "}
        {activeTab === "outbound"
          ? outboundVariant.endStop
          : returnVariant.endStop}
      </p>
      <div className="mt-2 grid grid-cols-4 sm:grid-cols-6 md:grid-cols-8 lg:grid-cols-10 gap-2">
        {tripTimes.map((time, index) => (
          <div
            key={index}
            className="bg-teal-100 text-teal-800 px-2 py-1 rounded text-sm text-center"
          >
            {time}
          </div>
        ))}
      </div>
    </div>
  );
};

export default RouteSchedule;
