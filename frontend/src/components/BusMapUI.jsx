import React from 'react';
import SearchTabs from './SearchTabs';
import MapView from './FindNavigation/MapView';

export default function BusMapUI() {
  return (
    <div className="grid grid-cols-1 md:grid-cols-3 h-screen">
      <SearchTabs />
      <div className="col-span-2 relative">
        <MapView />
        <div className="absolute top-4 right-4 bg-white p-3 rounded-xl shadow-lg flex items-center gap-2 text-sm font-medium text-gray-700">
          <span>📍</span> Vị trí lân cận
        </div>
      </div>
    </div>
  );
}