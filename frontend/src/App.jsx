import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import SearchRoutes from "./pages/user/SearchRoutes";
import FavoriteRoutes from "./pages/user/FavoriteRoutes";
import RealTimeUpdates from "./pages/user/RealTimeUpdates";

import Navbar from "./components/navbar";



export default function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-100">
        <Navbar />
        <main className="p-4">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />



            {/* User routes */}
            <Route path="/search" element={<SearchRoutes />} />
            <Route path="/favorites" element={<FavoriteRoutes />} />
            <Route path="/realtime" element={<RealTimeUpdates />} />

          </Routes>
        </main>
      </div>
    </Router>
  );
}
