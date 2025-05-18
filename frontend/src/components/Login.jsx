import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import cookie from 'react-cookies';
import { Apis, authApis, endpoints } from '../configs/apis';
import { login } from '../features/user/userSlice';

const Login = () => {
  const [user, setUser] = useState({ email: '', password: '' });
  const [msg, setMsg] = useState('');
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
      const res = await Apis.post(endpoints['login'], { ...user });
      cookie.save('token', res.data.token);
      const u = await authApis().get(endpoints['current-user']);
      dispatch(login(u.data)); // Dispatch action login
      navigate('/');
    } catch (ex) {
      console.error(ex);
      setMsg('Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
      <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-sm">
        <h1 className="text-2xl font-semibold text-center mb-4">Sign In</h1>
        {msg && (
          <div className="bg-red-100 text-red-700 p-2 rounded mb-4 text-center text-sm">
            {msg}
          </div>
        )}
        <form onSubmit={loginUser} className="space-y-4">
          <div>
            <input
              type="email"
              value={user.email}
              onChange={(e) => setState(e.target.value, 'email')}
              placeholder="Email"
              required
              className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-400"
            />
          </div>
          <div>
            <input
              type="password"
              value={user.password}
              onChange={(e) => setState(e.target.value, 'password')}
              placeholder="Password"
              required
              className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-400"
            />
          </div>
          <div className="flex justify-between text-sm text-gray-600">
            <label className="flex items-center">
              <input type="checkbox" className="mr-1" />
              Remember me
            </label>
            <a href="/forgot-password" className="text-blue-500 hover:underline">
              Forgot password?
            </a>
          </div>
          {loading ? (
            <div className="flex justify-center">
              <svg
                className="animate-spin h-5 w-5 text-blue-500"
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
                ></circle>
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                ></path>
              </svg>
            </div>
          ) : (
            <button
              type="submit"
              className="w-full bg-blue-600 text-white p-2 rounded-lg hover:bg-blue-700 transition"
            >
              Sign In
            </button>
          )}
        </form>
        <p className="mt-4 text-center text-sm text-gray-600">
          Don't have an account?{' '}
          <a href="/register" className="text-blue-500 hover:underline">
            Sign up
          </a>
        </p>
      </div>
    </div>
  );
};

export default Login;