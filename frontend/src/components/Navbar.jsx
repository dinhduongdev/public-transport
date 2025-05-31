import { useSelector, useDispatch } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { logout } from "../features/user/userSlice";
import {
  fetchNotifications,
  markNotificationAsRead,
} from "../features/notifications/notificationSlice";
import {
  FaBus,
  FaUserCircle,
  FaSignOutAlt,
  FaSignInAlt,
  FaUserPlus,
  FaBell,
} from "react-icons/fa";
import { toast } from "react-toastify";
import { useState, useEffect, useRef } from "react";
import { formatCreatedAt } from "../utils/formatCreatedAt";

const Navbar = () => {
  const user = useSelector((state) => state.user);
  const { notifications, unreadCount, loading } = useSelector(
    (state) => state.notification
  );
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  // Yêu cầu quyền thông báo đẩy
  const requestNotificationPermission = async () => {
    if (!("Notification" in window)) {
      console.log("Trình duyệt không hỗ trợ thông báo đẩy.");
      return;
    }
    const permission = await Notification.requestPermission();
    if (permission !== "granted") {
      console.log("Quyền thông báo đẩy bị từ chối.");
    }
  };

  // Hiển thị thông báo đẩy
  const showPushNotification = (notification) => {
    if (Notification.permission === "granted") {
      new Notification(notification.title, {
        body: notification.message,
        icon: "/notification-icon.png",
      });
    }
  };

  // Đóng dropdown khi nhấp ra ngoài
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  //Lấy thông báo khi user thay đổi
  useEffect(() => {
    if (user?.id) {
      requestNotificationPermission();
      dispatch(fetchNotifications(user.id));
      // Polling để kiểm tra thông báo mới (mỗi 30 giây)
      // const interval = setInterval(() => {
      //   dispatch(fetchNotifications(user.id));
      // }, 30000);
      // return () => clearInterval(interval);
    }
  }, [user, dispatch]);

  // Hiển thị thông báo đẩy khi có thông báo mới
  useEffect(() => {
    notifications.forEach((notification) => {
      if (!notification.isRead) {
        showPushNotification(notification);
      }
    });
  }, [notifications]);

  const handleLogout = () => {
    dispatch(logout());
    toast.info("Bạn đã đăng xuất!");
    navigate("/login");
  };

  return (
    <nav className="bg-blue-700 p-4 shadow-lg">
      <div className="mx-auto flex justify-between items-center">
        <Link
          to="/"
          className="flex items-center space-x-2 text-white text-2xl font-bold"
        >
          <FaBus className="text-yellow-300" />
          <span>TransitEasy</span>
        </Link>
        <div className="flex space-x-6">
          <Link
            to="/"
            className="flex items-center text-white hover:text-yellow-300 transition"
          >
            <span>TRANG CHỦ</span>
          </Link>
          {user ? (
            <>
              {user.role === "admin" && (
                <Link
                  to="/admin/dashboard"
                  className="flex items-center text-white hover:text-yellow-300 transition"
                >
                  <span>Admin Dashboard</span>
                </Link>
              )}
              <Link
                to="/favorites"
                className="flex items-center space-x-1 text-white hover:text-yellow-300 transition"
              >
                <span>YÊU THÍCH</span>
              </Link>
              <Link
                to="/reports"
                className="flex items-center space-x-1 text-white hover:text-yellow-300 transition"
              >
                <span>BÁO CÁO GIAO THÔNG</span>
              </Link>
              {/* Dropdown thông báo */}
              <div className="relative flex" ref={dropdownRef}>
                <button
                  onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                  className="flex items-center space-x-1 text-white hover:text-yellow-300 transition relative cursor-pointer"
                >
                  <FaBell />
                  {unreadCount > 0 && (
                    <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                      {unreadCount}
                    </span>
                  )}
                </button>
                {isDropdownOpen && (
                  <div className="absolute right-0 mt-6 w-80 bg-white rounded-lg shadow-xl z-50 max-h-96 overflow-y-auto">
                    <div className="p-4">
                      <h3 className="text-lg font-semibold mb-2">
                        THÔNG BÁO
                      </h3>
                      {loading ? (
                        <p className="text-gray-500">Loading...</p>
                      ) : notifications.length === 0 ? (
                        <p className="text-gray-500">
                          Không có thông báo nào
                        </p>
                      ) : (
                        notifications
                        .slice()
                        .reverse()
                        .map((notification) => (
                          <div
                            key={notification.id}
                            className={`p-3 rounded-md mb-2 ${
                              notification.isRead
                                ? "bg-gray-100"
                                : "bg-blue-50 hover:bg-blue-100"
                            }`}
                          >
                            <h4
                              className={`text-sm font-medium ${
                                notification.isRead
                                  ? "text-gray-600"
                                  : "text-gray-900"
                              }`}
                            >
                              {notification.title}
                            </h4>
                            <p className="text-sm text-gray-500">
                              {notification.message}
                            </p>
                            <p className="text-xs text-gray-400">
                              {formatCreatedAt(
                                notification.createdAt
                              )} 
                            </p>
                            {!notification.isRead && (
                              <button
                                onClick={() =>
                                  dispatch(
                                    markNotificationAsRead(notification.id)
                                  )
                                }
                                className="text-xs text-blue-600 hover:text-blue-800 mt-1"
                              >
                                Đánh dấu đã đọc
                              </button>
                            )}
                          </div>
                        ))
                      )}
                    </div>
                  </div>
                )}
              </div>
              <Link
                to="/profile"
                className="flex items-center space-x-1 text-white hover:text-yellow-300 transition"
              >
                <FaUserCircle />
                <span>Chào, {user.lastname}</span>
              </Link>
              <button
                onClick={handleLogout}
                className="flex items-center space-x-1 text-white hover:text-yellow-300 transition cursor-pointer"
              >
                <FaSignOutAlt />
                <span>Đăng xuất</span>
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="flex items-center space-x-1 text-white hover:text-yellow-300 transition"
              >
                <FaSignInAlt />
                <span>Đăng nhập</span>
              </Link>
              <Link
                to="/register"
                className="flex items-center space-x-1 text-white hover:text-yellow-300 transition"
              >
                <FaUserPlus />
                <span>Đăng ký</span>
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
