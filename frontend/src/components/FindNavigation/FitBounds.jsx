import { useEffect } from 'react';
import { useMap } from 'react-leaflet';

export default function FitBounds({ routes, selectedStopCoordinates }) {
  const map = useMap();

  useEffect(() => {
    if (selectedStopCoordinates) {
      // Zoom vào trạm được chọn 
      map.flyTo([selectedStopCoordinates.lat, selectedStopCoordinates.lng], 16, {
        duration: 1,
      });
    } else if (routes.length > 0) {
      // Zoom bao quát toàn bộ đường đi
      const bounds = routes.flatMap((route) => [
        [route.startCoordinates.lat, route.startCoordinates.lng],
        [route.endCoordinates.lat, route.endCoordinates.lng],
        ...route.hops.map((hop) => [
          hop.stop.station.coordinates.lat,
          hop.stop.station.coordinates.lng,
        ]),
      ]);
      if (bounds.length > 0) {
        map.fitBounds(bounds, { padding: [50, 50] });
      }
    }
  }, [routes, selectedStopCoordinates, map]);

  return null;
}