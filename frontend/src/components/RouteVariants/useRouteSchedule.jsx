import { useState, useEffect, useCallback } from 'react';

const useRouteSchedule = (route, activeTab) => {
  const [scheduleData, setScheduleData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchSchedule = useCallback(async (variantId) => {
    if (!variantId) return;
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8080/PublicTransport/api/route-variants/${variantId}`);
      if (!response.ok) {
        throw new Error('Không thể lấy dữ liệu lịch trình');
      }
      const data = await response.json();
      setScheduleData(data);
    } catch (err) {
      setError(err.message);
      setScheduleData(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const outboundVariant = route?.routeVariants?.find((v) => v.isOutbound);
    const returnVariant = route?.routeVariants?.find((v) => !v.isOutbound);
    const variantId = activeTab === 'outbound' ? outboundVariant?.id : returnVariant?.id;
    fetchSchedule(variantId);
  }, [route, activeTab, fetchSchedule]);

  return { scheduleData, loading, error };
};

export default useRouteSchedule;