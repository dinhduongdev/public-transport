import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { fetchRoutes } from '../features/routes/routeSlice';
import RouteSearch from './RouteSearch';
import RouteDetail from './RouteDetail';

export default function BusMapUI() {
  const dispatch = useDispatch();
  const { selectedRoute, loading, error } = useSelector((state) => state.busRoutes);

  useEffect(() => {
    dispatch(fetchRoutes());
  }, [dispatch]);

  if (loading) {
    return <div className="flex justify-center items-center h-screen">Loading...</div>;
  }

  if (error) {
    return <div className="flex justify-center items-center h-screen text-red-500">Error: {error}</div>;
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 h-screen">
      {selectedRoute ? <RouteDetail /> : <RouteSearch />}
      <div className="col-span-2 relative">
        <iframe
          title="Google Map"
          width="100%"
          height="100%"
          loading="lazy"
          style={{ border: 0 }}
          allowFullScreen
          src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3919.494066324302!2d106.6799838758711!3d10.773374659258782!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752f1d63a8adcd%3A0xa51fc27b34c8055!2zQmnDqm4gVGjDoG5oLCBC4buLY2ggTmh1LCBI4buTIENow60gTWluaCwgVmnhu4d0IE5hbQ!5e0!3m2!1svi!2s!4v1685536873232!5m2!1svi!2s"
        ></iframe>
        <div className="absolute top-4 right-4 bg-white p-2 rounded shadow">
          📍 Nearby Locations
        </div>
      </div>
    </div>
  );
}