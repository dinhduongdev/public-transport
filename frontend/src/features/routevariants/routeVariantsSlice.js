import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';

// Thunk để lấy dữ liệu lịch trình từ API
export const fetchRouteVariantSchedule = createAsyncThunk(
  'routeVariants/fetchRouteVariantSchedule',
  async (variantId, { rejectWithValue }) => {
    try {
      const response = await fetch(`http://localhost:8080/PublicTransport/api/route-variants/${variantId}`);
      if (!response.ok) {
        throw new Error('Không thể tải dữ liệu lịch trình.');
      }
      const data = await response.json();
      return { variantId, data };
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

const routeVariantsSlice = createSlice({
  name: 'routeVariants',
  initialState: {
    activeTab: 'outbound', // Tab "Lượt đi" hoặc "Lượt về"
    activeSubTab: 'schedule', // Sub-tab: "schedule", "stops", "info", "reviews"
    schedules: {}, // Lưu dữ liệu lịch trình theo variantId
    loading: false,
    error: null,
  },
  reducers: {
    setActiveTab: (state, action) => {
      state.activeTab = action.payload;
    },
    setActiveSubTab: (state, action) => {
      state.activeSubTab = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchRouteVariantSchedule.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchRouteVariantSchedule.fulfilled, (state, action) => {
        state.loading = false;
        state.schedules[action.payload.variantId] = action.payload.data;
      })
      .addCase(fetchRouteVariantSchedule.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { setActiveTab, setActiveSubTab } = routeVariantsSlice.actions;
export default routeVariantsSlice.reducer;