import React, { useContext, useState } from "react";
import { Link, useNavigate } from "react-router-dom"; // Fixed import
import { MyUserContext, MyDispatcherContext } from "../configs/MyContexts";

export default function Navbar() {
  const user = useContext(MyUserContext);
  const dispatch = useContext(MyDispatcherContext);
  const nav = useNavigate();
  const [isOpen, setIsOpen] = useState(false); // State for mobile menu toggle

  const logout = () => {
    dispatch({ type: "logout" });
    nav("/login");
  };

  const toggleMenu = () => {
    setIsOpen(!isOpen);
  };

  return (
    <nav className="bg-gradient-to-r from-blue-600 to-blue-800 text-white shadow-lg sticky top-0 z-50">
      <div className="container mx-auto px-4 py-3 flex justify-between items-center">
        {/* Logo/Title */}
        <h1 className="text-2xl font-extrabold tracking-tight">
          <Link to="/" className="hover:text-blue-200 transition-colors">
            Giao Thông Công Cộng
          </Link>
        </h1>

        {/* Desktop Menu */}
        <div className="hidden md:flex items-center space-x-6">
          <Link
            to="/"
            className="py-2 px-3 rounded-md hover:bg-blue-700 hover:text-blue-100 transition-all duration-300"
          >
            Trang chủ
          </Link>
          <Link
            to="/search"
            className="py-2 px-3 rounded-md hover:bg-blue-700 hover:text-blue-100 transition-all duration-300"
          >
            Tìm đường
          </Link>
          <Link
            to="/favorites"
            className="py-2 px-3 rounded-md hover:bg-blue-700 hover:text-blue-100 transition-all duration-300"
          >
            Yêu thích
          </Link>
          <Link
            to="/realtime"
            className="py-2 px-3 rounded-md hover:bg-blue-700 hover:text-blue-100 transition-all duration-300"
          >
            Thời gian thực
          </Link>
          <Link
            to="/profile"
            className="py-2 px-3 rounded-md hover:bg-blue-700 hover:text-blue-100 transition-all duration-300"
          >
            hồ sơ
          </Link>

          {user ? (
            <div className="flex items-center space-x-4">
              <span className="font-semibold text-blue-100">
                Chào, {user.lastname}
              </span>
              <button
                onClick={logout}
                className="py-2 px-4 bg-red-500 rounded-md hover:bg-red-600 transition-colors duration-300"
              >
                Đăng xuất
              </button>
            </div>
          ) : (
            <div className="flex items-center space-x-4">
              <Link
                to="/login"
                className="py-2 px-4 bg-green-500 rounded-md hover:bg-green-600 transition-colors duration-300"
              >
                Đăng nhập
              </Link>
              <Link
                to="/register"
                className="py-2 px-4 bg-blue-500 rounded-md hover:bg-blue-600 transition-colors duration-300"
              >
                Đăng ký
              </Link>
            </div>
          )}
        </div>

        {/* Mobile Menu Button */}
        <button
          className="md:hidden focus:outline-none"
          onClick={toggleMenu}
          aria-label="Toggle menu"
        >
          <svg
            className="w-6 h-6"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d={isOpen ? "M6 18L18 6M6 6l12 12" : "M4 6h16M4 12h16m-7 6h7"}
            />
          </svg>
        </button>
      </div>

      {/* Mobile Menu */}
      <div
        className={`md:hidden transition-all duration-300 ease-in-out ${
          isOpen ? "max-h-screen opacity-100" : "max-h-0 opacity-0 overflow-hidden"
        }`}
      >
        <div className="flex flex-col space-y-2 px-4 pb-4">
          <Link
            to="/"
            className="py-2 px-3 rounded-md hover:bg-blue-700 hover:text-blue-100 transition-all duration-300"
            onClick={toggleMenu}
          >
            Trang chủ
          </Link>
          <Link
            to="/search"
            className="py-2 px-3 rounded-md hover:bg-blue-700 hover:text-blue-100 transition-all duration-300"
            onClick={toggleMenu}
          >
            Tìm đường
          </Link>
          <Link
            to="/favorites"
            className="py-2 px-3 rounded-md hover:bg-blue-700 hover:text-blue-100 transition-all duration-300"
            onClick={toggleMenu}
          >
            Yêu thích
          </Link>
          <Link
            to="/realtime"
            className="py-2 px-3 rounded-md hover:bg-blue-700 hover:text-blue-100 transition-all duration-300"
            onClick={toggleMenu}
          >
            Thời gian thực
          </Link>

          {user ? (
            <>
              <span className="py-2 px-3 font-semibold text-blue-100">
                Chào, {user.username}
              </span>
              <button
                onClick={() => {
                  logout();
                  toggleMenu();
                }}
                className="py-2 px-4 bg-red-500 rounded-md hover:bg-red-600 transition-colors duration-300 text-left"
              >
                Đăng xuất
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="py-2 px-4 bg-green-500 rounded-md hover:bg-green-600 transition-colors duration-300"
                onClick={toggleMenu}
              >
                Đăng nhập
              </Link>
              <Link
                to="/register"
                className="py-2 px-4 bg-blue-500 rounded-md hover:bg-blue-600 transition-colors duration-300"
                onClick={toggleMenu}
              >
                Đăng ký
              </Link>
            </>
          )}
        </div>
      </div>

      {/* Custom CSS for additional polish */}
      <style jsx>{`
        nav {
          transition: all 0.3s ease;
        }
        .container {
          max-width: 1200px;
        }
        svg {
          transition: transform 0.3s ease;
        }
        a:hover,
        button:hover {
          transform: translateY(-1px);
        }
      `}</style>
    </nav>
  );
}