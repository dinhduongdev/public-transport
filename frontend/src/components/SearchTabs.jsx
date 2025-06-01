import React, { useState } from 'react';
import RouteSearch from './RouteSearch';
import FindRoute from './FindRoute';

export default function SearchTabs() {
  const [activeTab, setActiveTab] = useState('search');

  return (
    <div className="h-full flex flex-col">
      <div className="flex border-b border-gray-300" role="tablist" aria-label="Search Tabs">
        <button
          role="tab"
          aria-selected={activeTab === 'search'}
          aria-controls="search-panel"
          id="search-tab"
          className={`flex-1 text-sm font-semibold p-3 border-b-2 cursor-pointer ${
            activeTab === 'search'
              ? 'border-teal-500 text-teal-600'
              : 'border-transparent text-gray-600'
          } hover:text-teal-500`}
          onClick={() => setActiveTab('search')}
        >
          🔍 TRA CỨU
        </button>
        <button
          role="tab"
          aria-selected={activeTab === 'find-route'}
          aria-controls="find-route-panel"
          id="find-route-tab"
          className={`flex-1 text-sm font-semibold p-3 border-b-2 cursor-pointer ${
            activeTab === 'find-route'
              ? 'border-teal-500 text-teal-600'
              : 'border-transparent text-gray-600'
          } hover:text-teal-500`}
          onClick={() => setActiveTab('find-route')}
        >
          🗺️ TÌM ĐƯỜNG
        </button>
      </div>

      <div className="overflow-y-auto p-4 flex-grow">
        {activeTab === 'search' && <RouteSearch />}
        {activeTab === 'find-route' && <FindRoute />}
      </div>
    </div>
  );
}
