import React, { useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import cookie from "react-cookies";
import Apis, { authApis, endpoints } from "../configs/apis";
import { MyUserContext, MyDispatcherContext } from "../configs/MyContexts";

const UserProfile = () => {
  const user = useContext(MyUserContext);
  const dispatch = useContext(MyDispatcherContext);
  const nav = useNavigate();
  const [profile, setProfile] = useState({
    email: "",
    password: "", // Optional for updates
    avatar: null, // File for upload
    avatarUrl: "", // URL for display
  });
  const [msg, setMsg] = useState("");
  const [loading, setLoading] = useState(false);
  const [isEditing, setIsEditing] = useState(false);

  // Fetch user data on mount
  useEffect(() => {
    const fetchUser = async () => {
      try {
        setLoading(true);
        const res = await authApis().get(endpoints["current-user"]);
        setProfile({
          email: res.data.email || "",
          password: "",
          avatar: null,
          avatarUrl: res.data.avatar || "",
        });
        dispatch({ type: "login", payload: res.data }); // Update context
      } catch (ex) {
        console.error(ex);
        setMsg("Không thể tải thông tin người dùng.");
        if (ex.response?.status === 401) {
          cookie.remove("token");
          dispatch({ type: "logout" });
          nav("/login");
        }
      } finally {
        setLoading(false);
      }
    };

    if (user) {
      fetchUser();
    } else {
      nav("/login");
    }
  }, [user, dispatch, nav]);

  // Handle input changes
  const handleChange = (value, field) => {
    if (field === "avatar") {
      setProfile({
        ...profile,
        avatar: value, // File object
        avatarUrl: value ? URL.createObjectURL(value) : profile.avatarUrl, // Preview
      });
    } else {
      setProfile({ ...profile, [field]: value });
    }
  };

  // Handle profile update
  const updateProfile = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      const formData = new FormData();
      formData.append("email", profile.email);
      if (profile.password) {
        formData.append("password", profile.password);
      }
      if (profile.avatar) {
        formData.append("avatar", profile.avatar);
      }

      const res = await authApis().put(endpoints["update-user"], formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      dispatch({ type: "login", payload: res.data });
      setMsg("Cập nhật hồ sơ thành công!");
      setIsEditing(false);
      setProfile({
        ...profile,
        password: "",
        avatar: null,
        avatarUrl: res.data.avatar || profile.avatarUrl,
      });
    } catch (ex) {
      console.error(ex);
      setMsg("Cập nhật thất bại. Vui lòng kiểm tra lại thông tin.");
    } finally {
      setLoading(false);
    }
  };

  // Toggle edit mode
  const toggleEdit = () => {
    setIsEditing(!isEditing);
    setMsg("");
    if (isEditing && profile.avatar) {
      URL.revokeObjectURL(profile.avatarUrl); // Clean up preview
    }
  };

  // Loading state
  if (loading && !profile.email) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <svg
          className="animate-spin h-8 w-8 text-green-500"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <circle
            className="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            strokeWidth="4"
          />
          <path
            className="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
          />
        </svg>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
        <h1 className="text-3xl font-bold text-center text-blue-600 mb-6">
          Hồ Sơ Người Dùng
        </h1>

        {msg && (
          <div
            className={`p-3 rounded mb-4 text-center ${
              msg.includes("thành công")
                ? "bg-green-100 text-green-700"
                : "bg-red-100 text-red-700"
            }`}
          >
            {msg}
          </div>
        )}

        {isEditing ? (
          <form onSubmit={updateProfile} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Email
              </label>
              <input
                type="email"
                value={profile.email}
                onChange={(e) => handleChange(e.target.value, "email")}
                className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Mật khẩu mới (để trống nếu không đổi)
              </label>
              <input
                type="password"
                value={profile.password}
                onChange={(e) => handleChange(e.target.value, "password")}
                className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Nhập mật khẩu mới"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Ảnh đại diện
              </label>
              <input
                type="file"
                accept="image/*"
                onChange={(e) => handleChange(e.target.files[0], "avatar")}
                className="w-full p-2 border rounded-lg"
              />
              {profile.avatarUrl && (
                <img
                  src={profile.avatarUrl}
                  alt="Avatar Preview"
                  className="mt-2 w-24 h-24 rounded-full object-cover"
                />
              )}
            </div>

            <div className="flex space-x-4">
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-green-500 text-white p-2 rounded-lg hover:bg-green-600 transition-colors duration-300 disabled:bg-green-300"
              >
                {loading ? (
                  <svg
                    className="animate-spin h-5 w-5 mx-auto text-white"
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                  >
                    <circle
                      className="opacity-25"
                      cx="12"
                      cy="12"
                      r="10"
                      stroke="currentColor"
                      strokeWidth="4"
                    />
                    <path
                      className="opacity-75"
                      fill="currentColor"
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                    />
                  </svg>
                ) : (
                  "Lưu thay đổi"
                )}
              </button>
              <button
                type="button"
                onClick={toggleEdit}
                className="w-full bg-gray-500 text-white p-2 rounded-lg hover:bg-gray-600 transition-colors duration-300"
              >
                Hủy
              </button>
            </div>
          </form>
        ) : (
          <div className="space-y-4">
            {profile.avatarUrl && (
              <img
                src={profile.avatarUrl}
                alt="Avatar"
                className="w-24 h-24 rounded-full mx-auto mb-4 object-cover"
              />
            )}
            <div>
              <p className="text-sm font-medium text-gray-700">Email</p>
              <p className="text-lg font-semibold text-gray-900">
                {profile.email}
              </p>
            </div>
            <button
              onClick={toggleEdit}
              className="w-full bg-blue-500 text-white p-2 rounded-lg hover:bg-blue-600 transition-colors duration-300"
            >
              Chỉnh sửa hồ sơ
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserProfile;