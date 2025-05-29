import React from 'react';

const RouteTabs = ({ routeVariants, activeTab, setActiveTab }) => {
  const outboundVariant = routeVariants?.find((v) => v.isOutbound);
  const returnVariant = routeVariants?.find((v) => !v.isOutbound);

  return (
    <div className="flex gap-4 mb-4">
      {outboundVariant && (
        <button
          onClick={() => setActiveTab('outbound')}
          className={`px-4 py-2 rounded font-medium ${
            activeTab === 'outbound' ? 'bg-teal-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
          } transition-colors`}
        >
          Lượt đi
        </button>
      )}
      {returnVariant && (
        <button
          onClick={() => setActiveTab('return')}
          className={`px-4 py-2 rounded font-medium ${
            activeTab === 'return' ? 'bg-teal-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
          } transition-colors`}
        >
          Lượt về
        </button>
      )}
    </div>
  );
};

export default RouteTabs;