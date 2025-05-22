import { login, logout } from "../features/user/userSlice";
import { authApis, endpoints } from "../configs/apis";
import cookie from "react-cookies";

export const restoreUser = async (dispatch) => {
  const token = cookie.load("token");

  if (token) {
    try {
      const res = await authApis().get(endpoints["current-user"]);
      dispatch(login(res.data));
    } catch (err) {
      console.error("Lỗi xác thực token:", err);
      cookie.remove("token");
      dispatch(logout());
    }
  }
};
