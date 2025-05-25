import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { Apis, authApis, endpoints } from '../../configs/apis'; 

// Thunk để fetch dữ liệu đánh giá
export const fetchRatingData = createAsyncThunk(
  'ratings/fetchRatingData',
  async (routeId, { rejectWithValue }) => {
    try {
      const response = await authApis().get(endpoints.ratingSummary(routeId));
      return response.data;
    } catch (err) {
      if (err.response?.status === 401) {
        return rejectWithValue('Bạn cần đăng nhập để xem đánh giá.');
      }
      return rejectWithValue('Không thể tải thông tin đánh giá.');
    }
  }
);

// Thunk để gửi đánh giá
export const submitRating = createAsyncThunk(
  'ratings/submitRating',
  async ({ userId, routeId, score, comment }, { rejectWithValue }) => {
    try {
        console.log(`${endpoints.submitRating}?userId=${userId}&routeId=${routeId}&score=${score}&comment=${comment}`);
        
      await authApis().post(
        `${endpoints.submitRating}?userId=${userId}&routeId=${routeId}&score=${score}&comment=${comment}`,
        {}
      );
      return { routeId };
    } catch (err) {
      if (err.response?.status === 401) {
        return rejectWithValue('Bạn cần đăng nhập để gửi đánh giá.');
      }
      return rejectWithValue('Không thể gửi đánh giá. Vui lòng thử lại.');
    }
  }
);

const ratingSlice = createSlice({
  name: 'ratings',
  initialState: {
    ratingData: null,
    loading: false,
    error: null,
  },
  reducers: {
    clearRatingData(state) {
      state.ratingData = null;
      state.error = null;
      state.loading = false;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Rating Data
      .addCase(fetchRatingData.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchRatingData.fulfilled, (state, action) => {
        state.loading = false;
        state.ratingData = action.payload;
      })
      .addCase(fetchRatingData.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
        state.ratingData = null;
      })
      // Submit Rating
      .addCase(submitRating.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(submitRating.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(submitRating.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { clearRatingData } = ratingSlice.actions;
export default ratingSlice.reducer;