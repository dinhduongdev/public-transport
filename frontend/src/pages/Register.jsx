import React, { useState } from "react";

export default function Register() {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    role: "user",
    avatar: null,
  });

  const handleChange = (e) => {
    const { name, value, files } = e.target;
    setFormData({
      ...formData,
      [name]: files ? files[0] : value,
    });
  };

  const handleRegister = (e) => {
    e.preventDefault();
    console.log(formData);
    // Xử lý gửi dữ liệu và upload avatar
  };

  return (
    <div className="max-w-md mx-auto bg-white p-6 rounded-xl shadow-md">
      <h2 className="text-2xl font-bold mb-4">Đăng ký</h2>
      <form onSubmit={handleRegister} className="space-y-4">
        <input
          type="text"
          name="name"
          placeholder="Họ và tên"
          className="w-full border p-2 rounded"
          onChange={handleChange}
          required
        />
        <input
          type="email"
          name="email"
          placeholder="Email"
          className="w-full border p-2 rounded"
          onChange={handleChange}
          required
        />
        <input
          type="password"
          name="password"
          placeholder="Mật khẩu"
          className="w-full border p-2 rounded"
          onChange={handleChange}
          required
        />
        <select
          name="role"
          className="w-full border p-2 rounded"
          onChange={handleChange}
        >
          <option value="user">Người dùng</option>
          <option value="admin">Quản trị viên</option>
        </select>
        <input
          type="file"
          name="avatar"
          accept="image/*"
          className="w-full"
          onChange={handleChange}
        />
        <button type="submit" className="w-full bg-green-600 text-white py-2 rounded">
          Đăng ký
        </button>
      </form>
    </div>
  );
}
