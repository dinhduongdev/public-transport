import React, { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { fetchRouteDetails } from '../features/routes/routeSlice';
import { ClipLoader } from 'react-spinners';

function RouteSearch() {
  const [searchTerm, setSearchTerm] = useState('');
  const [visibleCount, setVisibleCount] = useState(4);
  const [isLoading, setIsLoading] = useState(false);
  const dispatch = useDispatch();
  const { busRoutes, routeVariantsMap } = useSelector((state) => state.busRoutes);

  const filteredRoutes = busRoutes.filter(
    (route) =>
      route.number.toLowerCase().includes(searchTerm.toLowerCase()) ||
      route.route.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const visibleRoutes = filteredRoutes.slice(0, visibleCount);

  const handleRouteClick = (routeId) => {
    dispatch(fetchRouteDetails(routeId));
  };

  const handleScroll = (e) => {
    const { scrollTop, scrollHeight, clientHeight } = e.target;
    if (scrollTop + clientHeight >= scrollHeight - 10 && !isLoading) {
      setIsLoading(true);
      setTimeout(() => {
        setVisibleCount((prev) => prev + 4);
        setIsLoading(false);
      }, 1000);
    }
  };

  return (
    <div
      className="col-span-1 bg-white border-r overflow-y-auto p-4"
      style={{ maxHeight: '100vh' }}
      onScroll={handleScroll}
    >
      <h2 className="text-xl font-semibold mb-4">Search Routes</h2>
      <input
        type="text"
        placeholder="Search for a route"
        className="w-full p-2 mb-4 border rounded"
        value={searchTerm}
        onChange={(e) => {
          setSearchTerm(e.target.value);
          setVisibleCount(4); 
        }}
      />
      <div className="space-y-4">
        {visibleRoutes.map((route) => {
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
        {isLoading && (
          <div className="flex justify-center py-4">
            <ClipLoader size={35} color="#3B82F6" />
          </div>
        )}
      </div>
    </div>
  );
}

export default RouteSearch;
