import { createSlice } from "@reduxjs/toolkit";
import cookie from "react-cookies";

const userSlice = createSlice({
  name: "user",
  initialState: null,
  reducers: {
    login(state, action) {
      return action.payload;
    },
    logout() {
      cookie.remove("token"); 
      return null;
    },
    addFavorite: (state, action) => {
      if (!state.favorites) state.favorites = [];
      state.favorites.push(action.payload);
    },
    removeFavorite: (state, action) => {
      state.favorites.splice(action.payload, 1);
    },
  },
});

export const { login, logout,  addFavorite, removeFavorite  } = userSlice.actions;
export default userSlice.reducer;
