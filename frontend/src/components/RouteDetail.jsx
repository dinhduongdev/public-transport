import React, { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { clearSelectedRoute } from '../features/routes/routeSlice';

function RouteDetail() {
  const [viewMode, setViewMode] = useState('outbound');
  const dispatch = useDispatch();
  const {
    selectedRoute,
    selectedVariants,
    selectedVariantStopsMap,
    variantSchedulesMap,
    scheduleTripsMap,
  } = useSelector((state) => state.busRoutes);

  // Filter RouteVariant based on view mode
  const selectedVariant = selectedVariants.find((variant) =>
    viewMode === 'outbound' ? variant.isOutbound : !variant.isOutbound
  );

  // Get schedules and trips for the selected RouteVariant
  const schedules = selectedVariant ? variantSchedulesMap[selectedVariant.id] || [] : [];
  const trips = schedules.flatMap((schedule) => scheduleTripsMap[schedule.id] || []);

  const handleBack = () => {
    dispatch(clearSelectedRoute());
  };

  return (
    <div className="col-span-1 bg-white border-r overflow-y-auto p-4">
      <button
        onClick={handleBack}
        className="mb-4 px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
      >
        Back
      </button>
      <h2 className="text-xl font-semibold mb-4">Route Details</h2>
      <div className="border rounded-lg p-3 shadow">
        <h3 className="text-blue-600 font-semibold">Route {selectedRoute.code}</h3>
        <p className="mb-2">{selectedRoute.name}</p>
        <p className="text-sm text-gray-600 mb-2">
          <strong>Type:</strong> {selectedRoute.type}
        </p>

        {/* Toggle between outbound and inbound */}
        <div className="flex gap-2 mb-4">
          <button
            onClick={() => setViewMode('outbound')}
            className={`px-4 py-2 rounded text-white ${
              viewMode === 'outbound' ? 'bg-blue-600' : 'bg-gray-400'
            } hover:bg-blue-700`}
          >
            Outbound
          </button>
          <button
            onClick={() => setViewMode('inbound')}
            className={`px-4 py-2 rounded text-white ${
              viewMode === 'inbound' ? 'bg-blue-600' : 'bg-gray-400'
            } hover:bg-blue-700`}
          >
            Inbound
          </button>
        </div>

        {/* Display route details and stops */}
        {selectedVariant ? (
          <div className="mt-4">
            <h4 className="text-md font-medium">{selectedVariant.name}</h4>
            <p className="text-sm text-gray-600">
              <strong>Distance:</strong> {(selectedVariant.distance / 1000).toFixed(2)} km
            </p>
            <p className="text-sm text-gray-600">
              <strong>Start:</strong> {selectedVariant.startStop}
            </p>
            <p className="text-sm text-gray-600">
              <strong>End:</strong> {selectedVariant.endStop}
            </p>
            <h5 className="text-sm font-medium mt-2">Stops:</h5>
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
            <h5 className="text-sm font-medium mt-4">Schedule:</h5>
            {trips.length > 0 ? (
              <div className="mt-2 flex flex-wrap gap-2">
                {trips.map((trip) => (
                  <div
                    key={trip.id}
                    className="p-2 bg-gray-200 border rounded text-sm text-center w-32"
                  >
                    <p>
                      <strong>Start:</strong> [{trip.startTime[0]}, {trip.startTime[1]}]
                    </p>
                    <p>
                      <strong>End:</strong> [{trip.endTime[0]}, {trip.endTime[1]}]
                    </p>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-sm text-gray-600">No trips available.</p>
            )}
          </div>
        ) : (
          <p className="text-sm text-gray-600">No route details to display.</p>
        )}
      </div>
    </div>
  );
}

export default RouteDetail;