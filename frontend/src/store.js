import { configureStore } from "@reduxjs/toolkit";
import userReducer from "./features/user/userSlice";
import busRoutesReducer from "./features/routes/routeSlice";
import ratingReducer from "./features/ratings/ratingSlice";
import favoritesReducer from "./features/favorites/favoritesSlice";
import notificationReducer from "./features/notifications/notificationSlice";
import routeVariantsReducer from "./features/routevariants/routeVariantsSlice";
import navigationReducer from "./features/routes/navigationSlice";
import trafficReportsReducer from "./features/trafficreport/trafficReportsSlice";

export const store = configureStore({
  reducer: {
    user: userReducer,
    busRoutes: busRoutesReducer,
    ratings: ratingReducer,
    favorites: favoritesReducer,
    notification: notificationReducer,
    routeVariants: routeVariantsReducer,
    navigation: navigationReducer,
    trafficReports: trafficReportsReducer,
  },
});
