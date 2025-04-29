import React from "react";

export default function BusMapUI() {
  const busRoutes = [
    {
      number: 15,
      route: "Chợ Phú Định - Đầm Sen",
      time: "05:00 - 19:00",
      price: "6.000 VNĐ",
    },
    {
      number: 16,
      route: "BX Chợ Lớn - BX Tân Phú",
      time: "05:00 - 19:00",
      price: "6.000 VNĐ",
    },
    {
      number: 18,
      route: "Bến Thành - Chợ Hiệp Thành",
      time: "04:30 - 20:30",
      price: "6.000 VNĐ",
    },
    {
      number: 19,
      route: "BX Sài Gòn - KCX Linh Trung",
      time: "04:00 - 20:00",
      price: "6.000 VNĐ",
    },
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 h-screen">
      {/* Sidebar tuyến xe buýt */}
      <div className="col-span-1 bg-white border-r overflow-y-auto p-4">
        <h2 className="text-xl font-semibold mb-4">TRA CỨU</h2>
        <input
          type="text"
          placeholder="Tìm tuyến xe"
          className="w-full p-2 mb-4 border rounded"
        />
        <div className="space-y-4">
          {busRoutes.map((route, index) => (
            <div
              key={index}
              className="border rounded-lg p-3 shadow hover:shadow-md transition"
            >
              <h3 className="text-green-600 font-semibold">
                Tuyến số {route.number}
              </h3>
              <p>{route.route}</p>
              <div className="flex justify-between text-sm text-gray-600 mt-1">
                <span>🕒 {route.time}</span>
                <span>💰 {route.price}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Bản đồ bên phải */}
      <div className="col-span-2 relative">
        <iframe
          title="Google Map"
          width="100%"
          height="100%"
          loading="lazy"
          style={{ border: 0 }}
          allowFullScreen
          src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3919.494066324302!2d106.6799838758711!3d10.773374659258782!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752f1d63a8adcd%3A0xa51fc27b34c8055!2zQmnDqm4gVGjDoG5oLCBC4buLY2ggTmh1LCBI4buTIENow60gTWluaCwgVmnhu4d0IE5hbQ!5e0!3m2!1svi!2s!4v1685536873232!5m2!1svi!2s"
        ></iframe>

        {/* Button nổi */}
        <div className="absolute top-4 right-4 bg-white p-2 rounded shadow">
          📍 Địa điểm xung quanh
        </div>
      </div>

    </div>
  );
}
