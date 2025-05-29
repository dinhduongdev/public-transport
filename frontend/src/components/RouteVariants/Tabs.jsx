import React from 'react';

const Tabs = ({ activeTab, setActiveTab }) => (
  <div className="mb-4 flex gap-4">
    {['outbound', 'return'].map((tab) => (
      <button
        key={tab}
        onClick={() => setActiveTab(tab)}
        className={`px-4 py-2 rounded ${
          activeTab === tab ? 'bg-teal-600 text-white' : 'bg-gray-200 text-gray-700'
        }`}
      >
        {tab === 'outbound' ? 'Lượt đi' : 'Lượt về'}
      </button>
    ))}
  </div>
);

export default Tabs;
