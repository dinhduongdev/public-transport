import React, { useState } from "react";
import Footer from "../../components/footer";

export default function SearchRoutes() {
  const [startPoint, setStartPoint] = useState("");
  const [endPoint, setEndPoint] = useState("");
  const [route, setRoute] = useState(null);

  const handleSearch = (e) => {
    e.preventDefault();
    // Tính toán lộ trình và cập nhật kết quả
    console.log("Tìm kiếm từ", startPoint, "đến", endPoint);
    // Đây sẽ là nơi gọi API hoặc tính toán lộ trình
    setRoute({
      start: startPoint,
      end: endPoint,
      path: ["Trạm A", "Trạm B", "Trạm C"],
      duration: "30 phút",
    });
  };

  return (
    <div className="max-w-md mx-auto bg-white p-6 rounded-xl shadow-md">
      <h2 className="text-2xl font-bold mb-4">Tìm tuyến đường</h2>
      <form onSubmit={handleSearch} className="space-y-4">
        <input
          type="text"
          placeholder="Điểm đi"
          className="w-full border p-2 rounded"
          value={startPoint}
          onChange={(e) => setStartPoint(e.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Điểm đến"
          className="w-full border p-2 rounded"
          value={endPoint}
          onChange={(e) => setEndPoint(e.target.value)}
          required
        />
        <button type="submit" className="w-full bg-blue-600 text-white py-2 rounded">
          Tìm kiếm
        </button>
      </form>

      {route && (
        <div className="mt-4">
          <h3 className="font-semibold">Lộ trình:</h3>
          <ul className="list-disc pl-6">
            {route.path.map((stop, index) => (
              <li key={index}>{stop}</li>
            ))}
          </ul>
          <p>Thời gian di chuyển: {route.duration}</p>
        </div>
      )}
      <Footer/>
    </div>
  );
}
