import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { authApis, endpoints } from '../../configs/apis';
import { toast } from 'react-toastify';

// Async thunk để lấy danh sách thông báo
export const fetchNotifications = createAsyncThunk(
  'notification/fetchNotifications',
  async (userId, { rejectWithValue }) => {
    try {
      const response = await authApis().get(endpoints.notificationsByUser(userId));
      return response.data;
    } catch (error) {
      toast.error('Không thể tải thông báo.');
      return rejectWithValue(error.response?.data || 'Error fetching notifications');
    }
  }
);

// Async thunk để đánh dấu thông báo là đã đọc
export const markNotificationAsRead = createAsyncThunk(
  'notification/markAsRead',
  async (notificationId, { rejectWithValue }) => {
    try {
      await authApis().patch(endpoints.notificationById(notificationId), { isRead: true });
      return notificationId;
    } catch (error) {
      toast.error('Không thể đánh dấu thông báo là đã đọc.');
      return rejectWithValue(error.response?.data || 'Error marking notification as read');
    }
  }
);

const notificationSlice = createSlice({
  name: 'notification',
  initialState: {
    notifications: [],
    unreadCount: 0,
    loading: false,
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    // Xử lý fetchNotifications
    builder
      .addCase(fetchNotifications.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchNotifications.fulfilled, (state, action) => {
        state.notifications = action.payload;
        state.unreadCount = action.payload.filter((n) => !n.isRead).length;
        state.loading = false;
      })
      .addCase(fetchNotifications.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Xử lý markNotificationAsRead
      .addCase(markNotificationAsRead.fulfilled, (state, action) => {
        const notificationId = action.payload;
        state.notifications = state.notifications.map((n) =>
          n.id === notificationId ? { ...n, isRead: true } : n
        );
        state.unreadCount = state.notifications.filter((n) => !n.isRead).length;
        toast.success('Thông báo đã được đánh dấu là đã đọc.');
      })
      .addCase(markNotificationAsRead.rejected, (state, action) => {
        state.error = action.payload;
      });
  },
});

export default notificationSlice.reducer;