import React, { useReducer } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { MyUserContext, MyDispatcherContext } from "./configs/MyContexts";
import reducer from "./reducers/MyUserReducer"; // Your reducer file
import Navbar from "./components/Navbar";
import Login from "./components/Login";
import Register from "./components/Register"; // Assuming you have this
import Home from "./pages/Home";
import UserProfile from "./pages/UserProfile";

const App = () => {
  const [user, dispatch] = useReducer(reducer, null); // Initialize state as null

  return (
    <MyUserContext.Provider value={user}>
      <MyDispatcherContext.Provider value={dispatch}>
        <BrowserRouter>
          <Navbar />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/profile" element={<UserProfile />} />
          </Routes>
        </BrowserRouter>
      </MyDispatcherContext.Provider>
    </MyUserContext.Provider>
  );
};

export default App;