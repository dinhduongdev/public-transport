import React from 'react';

const SubTabs = ({ activeSubTab, setActiveSubTab }) => {
  const tabs = [
    { key: 'schedule', label: 'Biểu đồ giờ', icon: '🕒' },
    { key: 'stops', label: 'Trạm dừng', icon: '🔴' },
    { key: 'info', label: 'Thông tin', icon: 'ℹ️' },
    { key: 'reviews', label: 'Đánh giá', icon: '⭐' },
  ];

  return (
    <div className="mb-4 flex gap-2">
      {tabs.map(({ key, label, icon }) => (
        <button
          key={key}
          onClick={() => setActiveSubTab(key)}
          className={`flex items-center gap-1 px-3 py-1 rounded text-sm ${
            activeSubTab === key ? 'bg-teal-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
          }`}
        >
          <span>{icon}</span>
          <span>{label}</span>
        </button>
      ))}
    </div>
  );
};

export default SubTabs;
