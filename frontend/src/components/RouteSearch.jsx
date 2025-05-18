import React, { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { fetchRouteDetails } from '../features/routes/routeSlice';

function RouteSearch() {
  const [searchTerm, setSearchTerm] = useState('');
  const dispatch = useDispatch();
  const { busRoutes, routeVariantsMap } = useSelector((state) => state.busRoutes);

  // Filter routes based on search term
  const filteredRoutes = busRoutes.filter(
    (route) =>
      route.number.toLowerCase().includes(searchTerm.toLowerCase()) ||
      route.route.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleRouteClick = (routeId) => {
    dispatch(fetchRouteDetails(routeId));
  };

  return (
    <div className="col-span-1 bg-white border-r overflow-y-auto p-4">
      <h2 className="text-xl font-semibold mb-4">Search Routes</h2>
      <input
        type="text"
        placeholder="Search for a route"
        className="w-full p-2 mb-4 border rounded"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />
      <div className="space-y-4">
        {filteredRoutes.map((route) => {
          const variants = routeVariantsMap[route.id] || [];
          return (
            <div
              key={route.id}
              onClick={() => handleRouteClick(route.id)}
              className="border rounded-lg p-3 shadow hover:shadow-md transition cursor-pointer"
            >
              <h3 className="text-blue-600 font-semibold">Route {route.number}</h3>
              <p>{route.route}</p>
              {variants.map((variant) => (
                <div key={variant.id} className="mt-2">
                  <p className="text-sm text-gray-600">
                    <strong>{variant.name}:</strong> {variant.startStop} → {variant.endStop}
                  </p>
                </div>
              ))}
              <div className="flex justify-between text-sm text-gray-600 mt-1">
                <span>🕒 {route.time}</span>
                <span>💰 {route.price}</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default RouteSearch;