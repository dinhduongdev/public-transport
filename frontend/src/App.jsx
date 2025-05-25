import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Login from "./components/Login";
import Register from "./components/Register"; // Assuming you have this
import Home from "./pages/Home";
import UserProfile from "./pages/user/UserProfile";
// import RouteSearch from "./components/RouteSearch";
import FavoriteRoutes from "./components/FavoriteRoutes";
import TrafficReport from "./components/TrafficReport";
import { useEffect } from "react";
import { restoreUser } from "./utils/authInit";
import { useDispatch } from "react-redux";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const App = () => {
  const dispatch = useDispatch();
  useEffect(() => {
    restoreUser(dispatch);
  }, [dispatch]);
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/favorites" element={<FavoriteRoutes />} />
        <Route path="/report-traffic" element={<TrafficReport />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/profile" element={<UserProfile />} />
      </Routes>
      <ToastContainer position="top-right" autoClose={3000} hideProgressBar />
    </BrowserRouter>
  );
};

export default App;
