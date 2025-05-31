import {createAsyncThunk, createSlice} from '@reduxjs/toolkit';
import {Apis, endpoints} from '../../configs/apis'; // Adjust the import path based on your project structure

// Async thunk to fetch route navigation data
export const fetchRouteNavigation = createAsyncThunk(
    'navigation/fetchRouteNavigation',
    async (params, {rejectWithValue}) => {
        try {
            const response = await Apis.get(endpoints.navigationRoute, {params});
            return response.data;
        } catch (error) {
            return rejectWithValue(error.response?.data || 'Failed to fetch route navigation');
        }
    }
);

const navigationSlice = createSlice({
    name: 'navigation',
    initialState: {
        routes: [],
        loading: false,
        error: null,
        selectedStopCoordinates: null, // Thêm state cho tọa độ trạm được chọn
    },
    reducers: {
        clearNavigation: (state) => {
            state.routes = [];
            state.error = null;
            state.selectedStopCoordinates = null; // Reset tọa độ khi clear
        },
        selectStop: (state, action) => {
            state.selectedStopCoordinates = action.payload; // Cập nhật tọa độ trạm
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(fetchRouteNavigation.pending, (state) => {
                state.loading = true;
                state.error = null;
                state.selectedStopCoordinates = null; // Reset tọa độ khi tìm kiếm mới
            })
            .addCase(fetchRouteNavigation.fulfilled, (state, action) => {
                state.loading = false;
                state.routes = action.payload;
            })
            .addCase(fetchRouteNavigation.rejected, (state, action) => {
                state.loading = false;
                state.error = action.payload;
            });
    },
});

export const {clearNavigation, selectStop} = navigationSlice.actions;
export default navigationSlice.reducer;