// import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
// import { Apis, endpoints } from "../../configs/apis";

// // Async thunk to fetch routes with pagination and search
// export const fetchRoutes = createAsyncThunk(
//   "busRoutes/fetchRoutes",
//   async ({ page = 1, size = 10, searchParams = {} }, { rejectWithValue }) => {
//     try {
//       const response = await Apis.get(endpoints.route, {
//         params: { page, size, ...searchParams },
//       });
//       return response.data;
//     } catch (error) {
//       return rejectWithValue(error.response?.data?.message || error.message);
//     }
//   }
// );

// const routesSlice = createSlice({
//   name: "busRoutes",
//   initialState: {
//     routes: [],
//     totalItems: 0,
//     totalPages: 0,
//     currentPage: 1,
//     status: "idle",
//     error: null,
//   },
//   reductions: {
//     setCurrentPage: (state, action) => {
//       state.currentPage = action.payload;
//     },
//   },
//   extraReducers: (builder) => {
//     builder
//       .addCase(fetchRoutes.pending, (state) => {
//         state.status = "loading";
//         state.error = null;
//       })
//       .addCase(fetchRoutes.fulfilled, (state, action) => {
//         state.status = "succeeded";
//         state.routes = action.payload.routes;
//         state.totalItems = action.payload.totalItems;
//         state.totalPages = action.payload.totalPages;
//         state.currentPage = action.payload.currentPage;
//       })
//       .addCase(
//         fetchRoutes.rejected,
//         (state, action) => {
//           state.status = "failed";
//           state.error = action.payload;
//         }
//       );
//   },
// });

// export const { setCurrentPage } = routesSlice.actions;
// export default routesSlice.reducer;


import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { Apis, endpoints } from "../../configs/apis";

// Async thunk to fetch routes with pagination and search
export const fetchRoutes = createAsyncThunk(
  "busRoutes/fetchRoutes",
  async ({ page = 1, size = 10, searchParams = {} }, { rejectWithValue }) => {
    try {
      const response = await Apis.get(endpoints.route, {
        params: { page, size, ...searchParams },
      });
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || error.message);
    }
  }
);

const routesSlice = createSlice({
  name: "busRoutes",
  initialState: {
    routes: [],
    totalItems: 0,
    totalPages: 0,
    currentPage: 1,
    status: "idle",
    error: null,
  },
  reducers: { // Fixed typo: changed "reductions" to "reducers"
    setCurrentPage: (state, action) => {
      state.currentPage = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchRoutes.pending, (state) => {
        state.status = "loading";
        state.error = null;
      })
      .addCase(fetchRoutes.fulfilled, (state, action) => {
        state.status = "succeeded";
        state.routes = action.payload.routes;
        state.totalItems = action.payload.totalItems;
        state.totalPages = action.payload.totalPages;
        state.currentPage = action.payload.currentPage;
      })
      .addCase(fetchRoutes.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.payload;
      });
  },
});

export const { setCurrentPage } = routesSlice.actions;
export default routesSlice.reducer;