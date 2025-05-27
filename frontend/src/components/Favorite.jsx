import { useSelector, useDispatch } from "react-redux";
import { useEffect, useState } from "react";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import {
  fetchFavorites,
  deleteFavorite,
  toggleObserved,
} from "../features/favorites/favoritesSlice";

const Favorite = () => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.user);
  const { favorites, loading, error } = useSelector((state) => state.favorites);
  const [activeTab, setActiveTab] = useState("ROUTE");

  useEffect(() => {
    if (user && user.id) {
      dispatch(fetchFavorites({ userId: user.id, targetType: activeTab }));
    } else {
      dispatch({ type: "favorites/fetchFavorites/fulfilled", payload: [] });
    }
  }, [dispatch, activeTab, user]);

  useEffect(() => {
    if (error) {
      toast.error(error);
    }
  }, [error]);

  const handleDeleteFavorite = (favoriteId) => {
    dispatch(deleteFavorite(favoriteId)).then((result) => {
      if (deleteFavorite.fulfilled.match(result)) {
        toast.success("Favorite deleted successfully");
      } else {
        toast.error("Failed to delete favorite");
      }
    });
  };

  const handleToggleObserved = (favoriteId, currentObserved) => {
    dispatch(toggleObserved({ favoriteId, currentObserved })).then((result) => {
      if (toggleObserved.fulfilled.match(result)) {
        const newObserved = result.payload.newObserved;
        toast.success(`Notifications ${newObserved ? "enabled" : "disabled"}`);
      } else {
        toast.error("Failed to update notification status");
      }
    });
  };

  const renderRouteItem = (fav) => {
    const { code, name, type } = fav.targetData || {};
    if (!code || !name || !type) return null;
    return (
      <li
        key={fav.id}
        className="flex items-center p-4 bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow"
      >
        <div className="flex-1">
          <span className="text-sm font-semibold text-blue-600">{type}</span>
          <div className="mt-1">
            <span className="font-bold text-gray-800">{code}</span> -{" "}
            <span className="text-gray-600">{name}</span>
          </div>
        </div>
        <div className="flex items-center space-x-2">
          <button
            onClick={() => handleToggleObserved(fav.id, fav.isObserved)}
            className={`px-3 py-1 text-sm rounded ${
              fav.isObserved
                ? "bg-yellow-500 text-white hover:bg-yellow-600"
                : "bg-gray-200 text-gray-700 hover:bg-gray-300"
            } transition-colors`}
            aria-label={
              fav.isObserved
                ? "Disable notifications for this favorite"
                : "Enable notifications for this favorite"
            }
          >
            {fav.isObserved ? "Off" : "On"}
          </button>
          <button
            onClick={() => handleDeleteFavorite(fav.id)}
            className="px-3 py-1 text-sm text-white bg-red-500 rounded hover:bg-red-600 transition-colors"
            aria-label="Delete this favorite"
          >
            Delete
          </button>
        </div>
      </li>
    );
  };

  const renderScheduleItem = (fav) => {
    const { routeVariant } = fav.targetData || {};
    const route = routeVariant?.route || {};
    if (!route.code || !route.name || !routeVariant) return null;
    return (
      <li
        key={fav.id}
        className="flex items-center p-4 bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow"
      >
        <div className="flex-1">
          <span className="text-sm font-semibold text-blue-600">{route.type}</span>
          <div className="mt-1">
            <span className="font-bold text-gray-800">{route.code}</span> -{" "}
            <span className="text-gray-600">{route.name}</span>{" "}
            <span className="text-sm text-gray-500">({routeVariant.name})</span>
          </div>
          <div className="mt-1 text-sm text-gray-600">
            {routeVariant.startStop} → {routeVariant.endStop}
          </div>
        </div>
        <div className="flex items-center space-x-2">
          <button
            onClick={() => handleToggleObserved(fav.id, fav.isObserved)}
            className={`px-3 py-1 text-sm rounded ${
              fav.isObserved
                ? "bg-yellow-500 text-white hover:bg-yellow-600"
                : "bg-gray-200 text-gray-700 hover:bg-gray-300"
            } transition-colors`}
            aria-label={
              fav.isObserved
                ? "Disable notifications for this favorite"
                : "Enable notifications for this favorite"
            }
          >
            {fav.isObserved ? "Off" : "On"}
          </button>
          <button
            onClick={() => handleDeleteFavorite(fav.id)}
            className="px-3 py-1 text-sm text-white bg-red-500 rounded hover:bg-red-600 transition-colors"
            aria-label="Delete this favorite"
          >
            Delete
          </button>
        </div>
      </li>
    );
  };

  const renderFavorites = () => {
    if (loading) {
      return (
        <div className="flex justify-center items-center h-32">
          <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-blue-600"></div>
        </div>
      );
    }
    if (favorites.length === 0) {
      return (
        <p className="text-center text-gray-500 py-8">
          No {activeTab.toLowerCase()} favorites found.
        </p>
      );
    }

    return (
      <ul className="space-y-3">
        {favorites
          .filter((fav) => fav.targetType === activeTab)
          .map((fav) =>
            activeTab === "ROUTE"
              ? renderRouteItem(fav)
              : renderScheduleItem(fav)
          )
          .filter(Boolean)}
      </ul>
    );
  };

  return (
    <div className="max-w-3xl mx-auto p-6 bg-gray-50 min-h-screen">
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Your Favorites</h2>
      <div className="flex mb-6 border-b border-gray-200">
        <button
          onClick={() => setActiveTab("ROUTE")}
          className={`px-4 py-2 text-sm font-medium transition-colors ${
            activeTab === "ROUTE"
              ? "border-b-2 border-blue-600 text-blue-600"
              : "text-gray-500 hover:text-gray-700"
          }`}
        >
          Routes
        </button>
        <button
          onClick={() => setActiveTab("SCHEDULE")}
          className={`px-4 py-2 text-sm font-medium transition-colors ${
            activeTab === "SCHEDULE"
              ? "border-b-2 border-blue-600 text-blue-600"
              : "text-gray-500 hover:text-gray-700"
          }`}
        >
          Schedules
        </button>
      </div>
      <div className="bg-gray-100 p-6 rounded-lg shadow-sm">
        {renderFavorites()}
      </div>
      {/* <ToastContainer
        position="bottom-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      /> */}
    </div>
  );
};

export default Favorite;