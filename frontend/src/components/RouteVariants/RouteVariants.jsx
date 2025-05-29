import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import RouteHeader from './RouteHeader';
import RouteTabs from './RouteTabs';
import RouteSubTabs from './RouteSubTabs';
import RouteContent from './RouteContent';
import RouteMap from './RouteMap';
import useRouteSchedule from './useRouteSchedule';

const RouteVariants = () => {
  const { state: { route } = {} } = useLocation();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('outbound');
  const [activeSubTab, setActiveSubTab] = useState('stops');

  const { scheduleData, loading, error } = useRouteSchedule(route, activeTab);

  if (!route || !route.routeVariants) {
    return <div className="p-4 text-red-500">Không có dữ liệu tuyến đường</div>;
  }

  return (
    <div className="p-4 h-screen flex flex-col">
      <RouteHeader route={route} onBack={() => navigate('/')} />
      <div className="flex flex-row flex-grow gap-4 overflow-hidden">
        <div className="w-3/10 flex flex-col overflow-y-auto pr-2">
          <RouteTabs routeVariants={route.routeVariants} activeTab={activeTab} setActiveTab={setActiveTab} />
          <RouteSubTabs activeSubTab={activeSubTab} setActiveSubTab={setActiveSubTab} />
          <RouteContent
            activeSubTab={activeSubTab}
            scheduleData={scheduleData}
            loading={loading}
            error={error}
            activeTab={activeTab}
            route={route}
          />
        </div>
        <div className="w-7/10 h-full">
          <h3 className="text-lg font-semibold mb-2">Bản đồ tuyến đường</h3>
          {loading ? (
            <div className="text-center py-4">
              <div className="inline-block animate-spin rounded-full h-6 w-6 border-t-2 border-b-2 border-teal-500"></div>
              <p className="text-gray-600 mt-2">Đang tải bản đồ...</p>
            </div>
          ) : error ? (
            <div className="bg-rose-50 border border-rose-200 text-rose-600 p-4 rounded-lg text-center">
              Lỗi: {error}
            </div>
          ) : scheduleData?.stops ? (
            <RouteMap stops={scheduleData.stops} />
          ) : (
            <div className="bg-gray-100 p-4 rounded-lg text-center text-gray-500">
              Không có dữ liệu bản đồ
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RouteVariants;
