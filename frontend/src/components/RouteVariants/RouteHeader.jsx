import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  addFavorite,
  fetchFavorites,
} from "../../features/favorites/favoritesSlice";
import { toast, ToastContainer } from "react-toastify";


const RouteHeader = ({ route, onBack, userId }) => {
  const dispatch = useDispatch();
  const { favorites, loading, error } = useSelector((state) => state.favorites);
  const isFavorite = favorites.some((fav) => fav.targetId === route?.id);

  console.log("id nhận được", userId);
  console.log("favorites", favorites);

  console.log(isFavorite);
  useEffect(() => {
    if (userId) {
      dispatch(fetchFavorites({ userId, targetType: "ROUTE" }));
    }
  }, [dispatch, userId]);

  const handleAddFavorite = async () => {
    try {
      await dispatch(
        addFavorite({
          userId,
          targetId: route.id,
          targetType: "ROUTE",
        })
      ).unwrap();
      toast.success("Thêm tuyến đường yêu thích thành công");
      // Optionally refetch favorites
      dispatch(fetchFavorites({ userId, targetType: "ROUTE" }));
    } catch (err) {
      console.error("Failed to add favorite:", err);
    }
  };

  return (
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
        <button
          onClick={handleAddFavorite}
          disabled={loading || isFavorite}
          className={`mt-2 px-4 py-2 text-white border rounded transition ${
            isFavorite
              ? "bg-gray-400 border-gray-500 cursor-not-allowed"
              : "bg-green-600 border-green-700 hover:bg-green-700 hover:border-green-800"
          }`}
        >
          {loading
            ? "Đang xử lý..."
            : isFavorite
            ? "Đã yêu thích"
            : "Thêm tuyến đường yêu thích"}
        </button>
        {error && <p className="text-red-500 mt-2">{error}</p>}
      </div>
    </div>
  );
};

export default RouteHeader;
