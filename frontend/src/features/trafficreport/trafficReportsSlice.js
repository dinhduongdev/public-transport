import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { authApis, endpoints } from '../../configs/apis';

//thunk để fetch traffic reports
export const fetchTrafficReports = createAsyncThunk(
  'trafficReports/fetchTrafficReports',
  async (_, { rejectWithValue }) => {
    try {
      const response = await authApis().get(endpoints.reports);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Lỗi khi lấy báo cáo');
    }
  }
);

//thunk để submit traffic report
export const submitTrafficReport = createAsyncThunk(
  'trafficReports/submitTrafficReport',
  async (reportData, { rejectWithValue }) => {
    try {
      const response = await authApis().post(endpoints.reports, reportData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Lỗi khi gửi báo cáo');
    }
  }
);

//thunk để reverse geocode
export const reverseGeocode = createAsyncThunk(
  'trafficReports/reverseGeocode',
  async ({ latitude, longitude }, { rejectWithValue }) => {
    try {
      const response = await authApis().get(
        `http://localhost:8080/PublicTransport/api/geocode/reverse?latitude=${latitude}&longitude=${longitude}`
      );
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data || 'Lỗi khi lấy địa chỉ');
    }
  }
);

// Tạo slice cho traffic reports
const trafficReportsSlice = createSlice({
  name: 'trafficReports',
  initialState: {
    loading: false,
    reports: [],
    error: null,
    submitLoading: false,
    submitError: null,
    geocodeLoading: false,
    geocodeError: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      // Xử lý fetchTrafficReports
      .addCase(fetchTrafficReports.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchTrafficReports.fulfilled, (state, action) => {
        state.loading = false;
        state.reports = action.payload;
      })
      .addCase(fetchTrafficReports.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Xử lý submitTrafficReport
      .addCase(submitTrafficReport.pending, (state) => {
        state.submitLoading = true;
        state.submitError = null;
      })
      .addCase(submitTrafficReport.fulfilled, (state, action) => {
        state.submitLoading = false;
        state.reports.push(action.payload);
      })
      .addCase(submitTrafficReport.rejected, (state, action) => {
        state.submitLoading = false;
        state.submitError = action.payload;
      })
      // Xử lý reverseGeocode
      .addCase(reverseGeocode.pending, (state) => {
        state.geocodeLoading = true;
        state.geocodeError = null;
      })
      .addCase(reverseGeocode.fulfilled, (state, action) => {
        state.geocodeLoading = false;
      })
      .addCase(reverseGeocode.rejected, (state, action) => {
        state.geocodeLoading = false;
        state.geocodeError = action.payload;
      });
  },
});

export default trafficReportsSlice.reducer;