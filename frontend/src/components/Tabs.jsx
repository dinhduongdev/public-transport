import React from 'react';

const Tabs = ({ activeTab, setActiveTab }) => {
  return (
    <div className="mb-4 flex gap-4">
      <button
        onClick={() => setActiveTab('outbound')}
        className={`px-4 py-2 rounded ${
          activeTab === 'outbound'
            ? 'bg-teal-600 text-white'
            : 'bg-gray-200 text-gray-700'
        }`}
      >
        Lượt đi
      </button>
      <button
        onClick={() => setActiveTab('return')}
        className={`px-4 py-2 rounded ${
          activeTab === 'return'
            ? 'bg-teal-600 text-white'
            : 'bg-gray-200 text-gray-700'
        }`}
      >
        Lượt về
      </button>
    </div>
  );
};

export default Tabs;