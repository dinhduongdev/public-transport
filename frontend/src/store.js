import { configureStore } from "@reduxjs/toolkit";
import userReducer from "./features/user/userSlice";
import busRoutesReducer from './features/routes/routeSlice';
import ratingReducer from './features/ratings/ratingSlice';


export const store = configureStore({
  reducer: {
    user: userReducer,
    busRoutes: busRoutesReducer,
    ratings: ratingReducer,
  },
});
