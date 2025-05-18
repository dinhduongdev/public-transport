import { useContext, useState } from "react";
import Apis, { authApis, endpoints } from "../configs/apis";
import { useNavigate } from "react-router-dom";
import cookie from "react-cookies";
import { MyDispatcherContext } from "../configs/MyContexts";

const Login = () => {
  const info = [
    {
      title: "Email",
      field: "email",
      type: "email",
    },
    {
      title: "Mật khẩu",
      field: "password",
      type: "password",
    },
  ];

  const [user, setUser] = useState({});
  const [msg, setMsg] = useState();
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();
  const dispatch = useContext(MyDispatcherContext);

  const setState = (value, field) => {
    setUser({ ...user, [field]: value });
  };

  const login = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      let res = await Apis.post(endpoints["login"], {
        ...user,
      });

      cookie.save("token", res.data.token);

      let u = await authApis().get(endpoints["current-user"]);
      console.info(u.data);

      dispatch({
        type: "login",
        payload: u.data,
      });
      nav("/");
    } catch (ex) {
      console.error(ex);
      setMsg("Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
      <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
        <h1 className="text-3xl font-bold text-center text-green-600 mb-6">
          ĐĂNG NHẬP
        </h1>

        {msg && (
          <div className="bg-red-100 text-red-700 p-3 rounded mb-4 text-center">
            {msg}
          </div>
        )}

        <form onSubmit={login} className="space-y-3">
          {info.map((i) => (
            <input
              key={i.field}
              type={i.type}
              value={user[i.field] || ""}
              onChange={(e) => setState(e.target.value, i.field)}
              placeholder={i.title}
              required
              className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 placeholder-gray-400"
            />
          ))}

          {loading ? (
            <div className="flex justify-center">
              <svg
                className="animate-spin h-5 w-5 text-green-500"
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
              className="w-full bg-green-500 text-white p-2 rounded-lg hover:bg-green-600 transition"
            >
              Đăng nhập
            </button>
          )}
        </form>

        <p className="mt-4 text-center text-sm text-gray-600">
          Chưa có tài khoản?{" "}
          <a href="/register" className="text-green-500 hover:underline">
            Đăng ký
          </a>
        </p>
      </div>
    </div>
  );
};

export default Login;