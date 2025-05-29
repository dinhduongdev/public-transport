import React, { lazy, Suspense } from 'react';
import RouteInfo from './RouteInfo';

// Lazy load components
const RouteSchedule = lazy(() => import('./RouteSchedule'));
const RouteStops = lazy(() => import('./RouteStops'));
const RouteReviews = lazy(() => import('./RouteReviews'));

const RouteContent = ({ activeSubTab, scheduleData, loading, error, activeTab, route }) => {
  const outboundVariant = route.routeVariants?.find((v) => v.isOutbound);
  const returnVariant = route.routeVariants?.find((v) => !v.isOutbound);

  const renderContent = () => {
    switch (activeSubTab) {
      case 'schedule':
        return (
          <div>
            <h3 className="text-lg font-semibold mb-2">Hôm nay</h3>
            <RouteSchedule
              scheduleData={scheduleData}
              loading={loading}
              error={error}
              activeTab={activeTab}
              outboundVariant={outboundVariant}
              returnVariant={returnVariant}
            />
          </div>
        );
      case 'stops':
        return (
          <div>
            <h3 className="text-lg font-semibold mb-2">Danh sách trạm dừng</h3>
            <RouteStops scheduleData={scheduleData} />
          </div>
        );
      case 'info':
        return (
          <div>
            <h3 className="text-lg font-semibold mb-2">Thông tin tuyến xe</h3>
            <RouteInfo routeVariant={scheduleData?.routeVariant} />
          </div>
        );
      case 'reviews':
        return (
          <div>
            <h3 className="text-lg font-semibold mb-2">Đánh giá</h3>
            <RouteReviews routeId={route.id} />
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <Suspense fallback={<div className="text-center py-4">Đang tải...</div>}>
      {renderContent()}
    </Suspense>
  );
};

export default RouteContent;