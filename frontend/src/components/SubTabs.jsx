import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { setActiveSubTab } from '../features/routevariants/routeVariantsSlice';

const SubTabs = ({ activeSubTab }) => {
  const dispatch = useDispatch();

  return (
    <div className="mb-4 flex gap-2">
      <button
        onClick={() => dispatch(setActiveSubTab('schedule'))}
        className={`flex items-center gap-1 px-3 py-1 rounded text-sm ${
          activeSubTab === 'schedule'
            ? 'bg-teal-600 text-white'
            : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
        }`}
      >
        <span>🕒</span>
        <span>Biểu đồ giờ</span>
      </button>
      <button
        onClick={() => dispatch(setActiveSubTab('stops'))}
        className={`flex items-center gap-1 px-3 py-1 rounded text-sm ${
          activeSubTab === 'stops'
            ? 'bg-teal-600 text-white'
            : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
        }`}
      >
        <span>🔴</span>
        <span>Trạm dừng</span>
      </button>
      <button
        onClick={() => dispatch(setActiveSubTab('info'))}
        className={`flex items-center gap-1 px-3 py-1 rounded text-sm ${
          activeSubTab === 'info'
            ? 'bg-teal-600 text-white'
            : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
        }`}
      >
        <span>ℹ️</span>
        <span>Thông tin</span>
      </button>
      <button
        onClick={() => dispatch(setActiveSubTab('reviews'))}
        className={`flex items-center gap-1 px-3 py-1 rounded text-sm ${
          activeSubTab === 'reviews'
            ? 'bg-teal-600 text-white'
            : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
        }`}
      >
        <span>⭐</span>
        <span>Đánh giá</span>
      </button>
    </div>
  );
};

export default SubTabs;