import React, { useState, useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { clearSelectedRoute } from "../features/routes/routeSlice";
import {
  fetchRatingData,
  clearRatingData,
} from "../features/ratings/ratingSlice";
import RouteReview from "./RouteReview";

function RouteDetail() {
  const [viewMode, setViewMode] = useState("outbound");
  const [tab, setTab] = useState("schedule");

  const dispatch = useDispatch();
  const {
    selectedRoute,
    selectedVariants,
    selectedVariantStopsMap,
    variantSchedulesMap,
    scheduleTripsMap,
  } = useSelector((state) => state.busRoutes);
  const { ratingData, loading, error } = useSelector((state) => state.ratings);

  // Fetch rating data khi selectedRoute thay đổi
  useEffect(() => {
    if (selectedRoute?.id) {
      dispatch(fetchRatingData(selectedRoute.id));
    } else {
      dispatch(clearRatingData());
    }
  }, [selectedRoute, dispatch]);

  const handleReviewSubmitted = () => {
    if (selectedRoute?.id) {
      dispatch(fetchRatingData(selectedRoute.id)); // refesh
    }
  };

  const selectedVariant = selectedVariants.find((variant) =>
    viewMode === "outbound" ? variant.isOutbound : !variant.isOutbound
  );

  const schedules = selectedVariant
    ? variantSchedulesMap[selectedVariant.id] || []
    : [];
  const trips = schedules.flatMap(
    (schedule) => scheduleTripsMap[schedule.id] || []
  );

  const handleBack = () => {
    dispatch(clearSelectedRoute());
  };

  return (
    <div className="col-span-1 bg-white border-r overflow-y-auto p-4">
      <button
        onClick={handleBack}
        className="mb-4 px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
      >
        Go Back
      </button>

      <h2 className="text-xl font-semibold mb-4">Route Details</h2>

      <div className="border rounded-lg p-3 shadow">
        <h3 className="text-blue-600 font-semibold">
          Route {selectedRoute.code}
        </h3>
        <p className="mb-2">{selectedRoute.name}</p>
        <p className="text-sm text-gray-600 mb-2">
          <strong>Type:</strong> {selectedRoute.type}
        </p>

        {/* Toggle outbound/inbound */}
        <div className="flex mb-4 rounded-md overflow-hidden border w-fit">
          <button
            onClick={() => setViewMode("outbound")}
            className={`px-4 py-2 text-sm font-medium transition ${
              viewMode === "outbound"
                ? "bg-blue-700 text-white"
                : "bg-white text-gray-800 hover:bg-gray-100"
            }`}
          >
            View Outbound
          </button>
          <button
            onClick={() => setViewMode("inbound")}
            className={`px-4 py-2 text-sm font-medium transition ${
              viewMode === "inbound"
                ? "bg-blue-700 text-white"
                : "bg-white text-gray-800 hover:bg-gray-100"
            }`}
          >
            View Inbound
          </button>
        </div>

        {/* Tabs */}
        <div className="flex gap-1 mb-4">
          {[
            { id: "schedule", label: "Timetable" },
            { id: "stops", label: "Stops" },
            { id: "info", label: "Information" },
            { id: "review", label: "Reviews" },
          ].map((item) => (
            <button
              key={item.id}
              onClick={() => setTab(item.id)}
              className={`px-3 py-1.5 rounded-md text-sm font-medium transition ${
                tab === item.id
                  ? "bg-gray-100 text-blue-600"
                  : "text-gray-700 hover:bg-gray-50"
              }`}
            >
              {item.label}
            </button>
          ))}
        </div>

        {/* Tab content */}
        {selectedVariant ? (
          <div className="mt-2 space-y-4">
            <h4 className="text-md font-medium">{selectedVariant.name}</h4>
            <p className="text-sm text-gray-600">
              <strong>Distance:</strong>{" "}
              {(selectedVariant.distance / 1000).toFixed(2)} km
            </p>

            {tab === "schedule" && (
              <>
                <h5 className="text-sm font-medium">Timetable:</h5>
                {trips.length > 0 ? (
                  <div className="flex flex-wrap gap-2">
                    {trips.map((trip) => (
                      <div
                        key={trip.id}
                        className="p-2 bg-gray-200 border rounded text-sm text-center w-32"
                      >
                        <p>
                          <strong>Start:</strong> [{trip.startTime[0]},{" "}
                          {trip.startTime[1]}]
                        </p>
                        <p>
                          <strong>End:</strong> [{trip.endTime[0]},{" "}
                          {trip.endTime[1]}]
                        </p>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-sm text-gray-600">No trips available.</p>
                )}
              </>
            )}

            {tab === "stops" && (
              <>
                <h5 className="text-sm font-medium">Stops:</h5>
                {selectedVariantStopsMap[selectedVariant.id]?.length > 0 ? (
                  <ul className="list-disc pl-5 text-sm text-gray-600">
                    {selectedVariantStopsMap[selectedVariant.id].map((stop) => (
                      <li key={stop.id}>
                        {stop.station.name} (Order: {stop.stopOrder})
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-sm text-gray-600">No stops available.</p>
                )}
              </>
            )}

            {tab === "info" && (
              <>
                <h5 className="text-sm font-medium">Route Information:</h5>
                <p className="text-sm text-gray-600">
                  <strong>Start:</strong> {selectedVariant.startStop}
                </p>
                <p className="text-sm text-gray-600">
                  <strong>End:</strong> {selectedVariant.endStop}
                </p>
              </>
            )}

            {tab === "review" && (
              <div>
                {loading ? (
                  <p className="text-sm text-gray-600">
                    Loading review information...
                  </p>
                ) : error ? (
                  <div className="flex items-center gap-2">
                    <p className="text-sm text-red-600">{error}</p>
                    <a
                      href="/login"
                      className="text-sm text-blue-600 hover:underline"
                    >
                      Sign Up
                    </a>
                  </div>
                ) : ratingData ? (
                  <RouteReview
                    summary={{
                      averageScore: ratingData.averageScore,
                      totalRatings: ratingData.totalRatings,
                      ratingDistribution: ratingData.ratingDistribution,
                    }}
                    reviews={ratingData.ratings}
                    routeId={selectedRoute.id}
                    onReviewSubmitted={handleReviewSubmitted}
                  />
                ) : (
                  <p className="text-sm text-gray-600">There are no reviews yet.</p>
                )}
              </div>
            )}
          </div>
        ) : (
          <p className="text-sm text-gray-600">No route data available.</p>
        )}
      </div>
    </div>
  );
}

export default RouteDetail;
