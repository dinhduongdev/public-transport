import React, { useState } from "react";

export default function FavoriteRoutes() {
  const [favoriteRoutes, setFavoriteRoutes] = useState([
    { start: "Trạm A", end: "Trạm B", duration: "15 phút" },
    { start: "Trạm C", end: "Trạm D", duration: "20 phút" },
  ]);

  // Hàm để xóa tuyến đường yêu thích
  const removeFavoriteRoute = (index) => {
    const newRoutes = favoriteRoutes.filter((_, i) => i !== index);
    setFavoriteRoutes(newRoutes);
  };

  return (
    <div className="max-w-md mx-auto bg-white p-6 rounded-xl shadow-md">
      <h2 className="text-2xl font-bold mb-4">Tuyến đường yêu thích</h2>
      <ul className="space-y-4">
        {favoriteRoutes.map((route, index) => (
          <li key={index} className="p-4 border rounded-lg">
            <p><strong>{route.start} - {route.end}</strong></p>
            <p>Thời gian di chuyển: {route.duration}</p>
            <button
              onClick={() => removeFavoriteRoute(index)}
              className="mt-2 bg-red-600 text-white py-1 px-4 rounded"
            >
              Xóa
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}
