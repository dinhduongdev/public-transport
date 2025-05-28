import React from "react";
import { useSelector } from "react-redux";

const Stops = () => {
  const activeTab = useSelector((state) => state.routeVariants.activeTab);
  console.log("activeTab", activeTab);

  const variantId = useSelector((state) => {

    console.log(state);
    
    activeTab === "outbound"
      ? state.busRoutes.routes
          .find((r) => r.id === state.location?.state?.route?.id)
          ?.routeVariants.find((v) => v.isOutbound)?.id
      : state.busRoutes.routes
          .find((r) => r.id === state.location?.state?.route?.id)
          ?.routeVariants.find((v) => !v.isOutbound)?.id;
  });

  console.log("====================================");
  console.log(variantId);
  console.log("====================================");
  const scheduleData = useSelector(
    (state) => state.routeVariants.schedules[variantId]
  );

  if (!scheduleData || !scheduleData.stops || scheduleData.stops.length === 0) {
    return <p>Không có thông tin trạm dừng.</p>;
  }

  return (
    <div>
      <h3 className="text-lg font-semibold mb-2">Danh sách trạm dừng</h3>
      <div className="space-y-2">
        {scheduleData.stops.map((stop) => (
          <div
            key={stop.id}
            className="p-4 border rounded-lg shadow bg-gray-50"
          >
            <p>
              <strong>{stop.station.name}</strong>
            </p>
            <p>
              {stop.station.location.address}, {stop.station.location.street},{" "}
              {stop.station.location.ward
                ? `${stop.station.location.ward}, `
                : ""}
              {stop.station.location.zone}
            </p>
            <p>
              <strong>Thứ tự:</strong> {stop.stopOrder}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Stops;
