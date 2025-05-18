// import { useSelector, useDispatch } from 'react-redux';
// import { Link, useNavigate } from 'react-router-dom';
// import { logout } from '../features/user/userSlice';

// const Navbar = () => {
//   const user = useSelector((state) => state.user);
//   const dispatch = useDispatch();
//   const navigate = useNavigate();

//   const handleLogout = () => {
//     dispatch(logout());
//     navigate('/login');
//   };

//   return (
//     <nav className="bg-green-600 p-4">
//       <div className="container mx-auto flex justify-between items-center">
//         <Link to="/" className="text-white text-xl font-bold">
//           MyApp
//         </Link>
//         <div className="space-x-4">
//           <Link to="/" className="text-white hover:underline">
//             Trang Chủ
//           </Link>
//           {user ? (
//             <>
//               <Link to="/profile" className="text-white hover:underline">
//                 Xin chào, {user.firstName}
//               </Link>
//               <button
//                 onClick={handleLogout}
//                 className="text-white hover:underline"
//               >
//                 Đăng xuất
//               </button>
//             </>
//           ) : (
//             <>
//               <Link to="/login" className="text-white hover:underline">
//                 Đăng nhập
//               </Link>
//               <Link to="/register" className="text-white hover:underline">
//                 Đăng ký
//               </Link>
//             </>
//           )}
//         </div>
//       </div>
//     </nav>
//   );
// };

// export default Navbar;
import { useSelector, useDispatch } from 'react-redux';
import { Link, useNavigate } from 'react-router-dom';
import { logout } from '../features/user/userSlice';
import { FaBus, FaUserCircle, FaSignOutAlt, FaSignInAlt, FaUserPlus } from 'react-icons/fa';

const Navbar = () => {
  const user = useSelector((state) => state.user);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  return (
    <nav className="bg-blue-700 p-4 shadow-lg">
      <div className="container mx-auto flex justify-between items-center">
        <Link to="/" className="flex items-center space-x-2 text-white text-2xl font-bold">
          <FaBus className="text-yellow-300" />
          <span>TransitEasy</span>
        </Link>
        <div className="flex space-x-6">
          <Link to="/" className="flex items-center text-white hover:text-yellow-300 transition">
            <span>Home</span>
          </Link>
          {user ? (
            <>
              {user.role === 'admin' && (
                <Link to="/admin/dashboard" className="flex items-center text-white hover:text-yellow-300 transition">
                  <span>Admin Dashboard</span>
                </Link>
              )}
              <Link to="/profile" className="flex items-center space-x-1 text-white hover:text-yellow-300 transition">
                <FaUserCircle />
                <span>Hello, {user.lastname}</span>
              </Link>
              <button
                onClick={handleLogout}
                className="flex items-center space-x-1 text-white hover:text-yellow-300 transition"
              >
                <FaSignOutAlt />
                <span>Logout</span>
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="flex items-center space-x-1 text-white hover:text-yellow-300 transition">
                <FaSignInAlt />
                <span>Login</span>
              </Link>
              <Link to="/register" className="flex items-center space-x-1 text-white hover:text-yellow-300 transition">
                <FaUserPlus />
                <span>Register</span>
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;