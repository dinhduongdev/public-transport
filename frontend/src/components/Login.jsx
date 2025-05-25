import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { GoogleLogin } from "@react-oauth/google";
import { jwtDecode } from "jwt-decode";
import { authApis, Apis, endpoints } from "../configs/apis";
import { login } from "../features/user/userSlice";
import cookie from "react-cookies";
import { toast } from "react-toastify";

const Login = () => {
  const [user, setUser] = useState({ email: "", password: "" });
  const [msg, setMsg] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const setState = (value, field) => {
    setUser({ ...user, [field]: value });
  };

  const loginUser = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      const res = await Apis.post(endpoints["login"], { ...user });
      cookie.save("token", res.data.token);
      const u = await authApis().get(endpoints["current-user"]);
      if (u.data.role !== "USER") {
        throw new Error("Chỉ người dùng client mới được phép đăng nhập ở đây.");
      }
      dispatch(login(u.data));
      toast.success("Đăng nhập thành công!");
      navigate("/");
    } catch (error) {
      console.error(error);
      setMsg("Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.");
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = async (credentialResponse) => {
    try {
      setLoading(true);
      const decoded = jwtDecode(credentialResponse.credential);
      const res = await Apis.post(endpoints["google-login"], {
        email: decoded.email,
        name: decoded.name,
        avatar: decoded.picture,
      });

      if (res.data.token) {
        cookie.save("token", res.data.token);
      }

      const u = await authApis().get(endpoints["current-user"]);
      if (u.data.role !== "USER") {
        throw new Error("Chỉ người dùng client mới được phép đăng nhập ở đây.");
      }

      dispatch(login(u.data));
      toast.success("Đăng nhập thành công!");
      navigate("/");
    } catch (error) {
      console.error("Lỗi khi đăng nhập bằng Google:", error);
      setMsg("Đăng nhập bằng Google thất bại. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
      <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-sm">
        <h1 className="text-2xl font-semibold text-center mb-4">
          Đăng nhập Client
        </h1>

        {msg && (
          <div className="bg-red-100 text-red-700 p-2 rounded mb-4 text-center text-sm">
            {msg}
          </div>
        )}

        <form onSubmit={loginUser} className="space-y-4">
          <input
            type="email"
            value={user.email}
            onChange={(e) => setState(e.target.value, "email")}
            placeholder="Email"
            required
            className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-400"
          />
          <input
            type="password"
            value={user.password}
            onChange={(e) => setState(e.target.value, "password")}
            placeholder="Password"
            required
            className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-400"
          />
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white p-2 rounded-lg hover:bg-blue-700 transition"
          >
            {loading ? "Đang xử lý..." : "Đăng nhập"}
          </button>
        </form>

        <div className="my-4 text-center text-sm text-gray-600">
          Hoặc đăng nhập bằng
        </div>

        <div className="flex justify-center">
          <GoogleLogin
            redirect_uri="http://localhost:5173/client-login"
            onSuccess={handleGoogleLogin}
            onError={() => {
              setMsg("Đăng nhập bằng Google thất bại.");
            }}
          />
        </div>

        <p className="mt-4 text-center text-sm text-gray-600">
          Chưa có tài khoản?{" "}
          <a href="/register" className="text-blue-500 hover:underline">
            Đăng ký
          </a>
        </p>
      </div>
    </div>
  );
};

export default Login;
