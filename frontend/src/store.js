import { configureStore } from "@reduxjs/toolkit";
import userReducer from "./features/user/userSlice";
import busRoutesReducer from './features/routes/routeSlice';

export const store = configureStore({
  reducer: {
    user: userReducer,
    busRoutes: busRoutesReducer,
  },
});
