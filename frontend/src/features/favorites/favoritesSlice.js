import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { authApis, endpoints } from "../../configs/apis";

const api = authApis();

export const fetchFavorites = createAsyncThunk(
  "favorites/fetchFavorites",
  async ({ userId, targetType }, { rejectWithValue }) => {
    try {
      const url = endpoints.favoritesByUser(userId, targetType);
      const res = await api.get(url);
      return Array.isArray(res.data) ? res.data : [];
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch favorites");
    }
  }
);

export const deleteFavorite = createAsyncThunk(
  "favorites/deleteFavorite",
  async (favoriteId, { rejectWithValue }) => {
    try {
      const url = endpoints.favoriteById(favoriteId);
      await api.delete(url);
      return favoriteId;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || "Failed to delete favorite");
    }
  }
);

export const toggleObserved = createAsyncThunk(
  "favorites/toggleObserved",
  async ({ favoriteId, currentObserved }, { rejectWithValue }) => {
    const newObserved = !currentObserved;
    try {
      const url = `${endpoints.favoriteById(favoriteId)}?observed=${newObserved}`;
      await api.patch(url, null);
      return { favoriteId, newObserved };
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || "Failed to update notification status");
    }
  }
);

export const addFavorite = createAsyncThunk(
  "favorites/addFavorite",
  async ({ userId, targetId, targetType }, { rejectWithValue }) => {
    try {
      const favorite = {
        user: {
            id: userId
        },
        targetType,
        targetId,
        isObserved: true,
      };
      const res = await api.post(endpoints.favorites, favorite);
      return res.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || "Failed to add favorite");
    }
  }
);

const favoritesSlice = createSlice({
  name: "favorites",
  initialState: {
    favorites: [],
    loading: false,
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchFavorites.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchFavorites.fulfilled, (state, action) => {
        state.loading = false;
        state.favorites = action.payload;
      })
      .addCase(fetchFavorites.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(deleteFavorite.fulfilled, (state, action) => {
        state.favorites = state.favorites.filter(
          (fav) => fav.id !== action.payload
        );
      })
      .addCase(toggleObserved.fulfilled, (state, action) => {
        const { favoriteId, newObserved } = action.payload;
        state.favorites = state.favorites.map((fav) =>
          fav.id === favoriteId ? { ...fav, isObserved: newObserved } : fav
        );
      })
      .addCase(addFavorite.fulfilled, (state, action) => {
        state.favorites.push(action.payload);
      })
      .addCase(addFavorite.rejected, (state, action) => {
        state.error = action.payload;
      });
  },
});

export default favoritesSlice.reducer;