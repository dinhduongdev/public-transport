import React from 'react';

const subTabs = [
  { id: 'schedule', label: 'Biểu đồ giờ', icon: '🕒' },
  { id: 'stops', label: 'Trạm dừng', icon: '🔴' },
  { id: 'info', label: 'Thông tin', icon: 'ℹ️' },
  { id: 'reviews', label: 'Đánh giá', icon: '⭐' },
];

const RouteSubTabs = ({ activeSubTab, setActiveSubTab }) => (
  <div className="mb-4 flex gap-2 flex-wrap">
    {subTabs.map((tab) => (
      <button
        key={tab.id}
        onClick={() => setActiveSubTab(tab.id)}
        className={`flex items-center gap-1 px-1 py-1 rounded text-sm font-medium ${
          activeSubTab === tab.id
            ? 'bg-teal-600 text-white'
            : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
        } transition-colors`}
      >
        <span>{tab.icon}</span>
        <span>{tab.label}</span>
      </button>
    ))}
  </div>
);

export default RouteSubTabs;