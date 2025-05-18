import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { Apis, endpoints } from '../../configs/apis';

// Async thunk to fetch all routes
export const fetchRoutes = createAsyncThunk('busRoutes/fetchRoutes', async (_, { rejectWithValue }) => {
  try {
    const response = await Apis.get(endpoints.route);
    const data = response.data;
    return {
      routes: data.routes.map(route => ({
        id: route.id,
        number: route.code,
        route: route.name,
        time: '05:00 - 19:00',
        price: '6.000 VNĐ',
      })),
      routeVariantsMap: data.routeVariantsMap || {},
    };
  } catch (error) {
    return rejectWithValue(error.response?.data?.message || error.message);
  }
});

// Async thunk to fetch route details
export const fetchRouteDetails = createAsyncThunk('busRoutes/fetchRouteDetails', async (routeId, { rejectWithValue }) => {
  try {
    const response = await Apis.get(endpoints.routeDetail(routeId));
    const data = response.data;
    return {
      route: data.route || null,
      variants: data.variants || [],
      variantStopsMap: data.variantStopsMap || {},
      variantSchedulesMap: data.variantSchedulesMap || {},
      scheduleTripsMap: data.scheduleTripsMap || {},
    };
  } catch (error) {
    return rejectWithValue(error.response?.data?.message || error.message);
  }
});

const busRoutesSlice = createSlice({
  name: 'busRoutes',
  initialState: {
    busRoutes: [],
    routeVariantsMap: {},
    selectedRoute: null,
    selectedVariants: [],
    selectedVariantStopsMap: {},
    variantSchedulesMap: {},
    scheduleTripsMap: {},
    loading: false,
    error: null,
  },
  reducers: {
    clearSelectedRoute: (state) => {
      state.selectedRoute = null;
      state.selectedVariants = [];
      state.selectedVariantStopsMap = {};
      state.variantSchedulesMap = {};
      state.scheduleTripsMap = {};
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch all routes
      .addCase(fetchRoutes.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchRoutes.fulfilled, (state, action) => {
        state.loading = false;
        state.busRoutes = action.payload.routes;
        state.routeVariantsMap = action.payload.routeVariantsMap;
      })
      .addCase(fetchRoutes.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Fetch route details
      .addCase(fetchRouteDetails.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchRouteDetails.fulfilled, (state, action) => {
        state.loading = false;
        state.selectedRoute = action.payload.route;
        state.selectedVariants = action.payload.variants;
        state.selectedVariantStopsMap = action.payload.variantStopsMap;
        state.variantSchedulesMap = action.payload.variantSchedulesMap;
        state.scheduleTripsMap = action.payload.scheduleTripsMap;
      })
      .addCase(fetchRouteDetails.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
        state.selectedRoute = null;
        state.selectedVariants = [];
        state.selectedVariantStopsMap = {};
        state.variantSchedulesMap = {};
        state.scheduleTripsMap = {};
      });
  },
});

export const { clearSelectedRoute } = busRoutesSlice.actions;
export default busRoutesSlice.reducer;