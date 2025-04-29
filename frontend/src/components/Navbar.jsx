import React from "react";
import { Link } from "react-router-dom";

export default function Navbar() {
  return (
    <nav className="bg-blue-600 text-white px-4 py-2 flex justify-between items-center">
      <h1 className="text-xl font-bold">Giao thông công cộng</h1>
      <div className="space-x-4">
        <Link to="/">Trang chủ</Link>
        <Link to="/search">Tìm đường</Link>
        <Link to="/favorites">Yêu thích</Link>
        <Link to="/realtime">Thời gian thực</Link>
        <Link to="/login">Đăng nhập</Link>
        <Link to="/register">Đăng ký</Link>
      </div>
    </nav>
  );
}
