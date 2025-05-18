import { createSlice } from '@reduxjs/toolkit';

const scheduleSlice = createSlice({
  name: 'schedules',
  initialState: [],
  reducers: {
    addSchedule: (state, action) => {
      state.push(action.payload);
    },
    updateSchedule: (state, action) => {
      const index = state.findIndex((s) => s.id === action.payload.id);
      if (index !== -1) state[index] = action.payload;
    },
    deleteSchedule: (state, action) => {
      return state.filter((s) => s.id !== action.payload);
    },
  },
});

export const { addSchedule, updateSchedule, deleteSchedule } = scheduleSlice.actions;
export default scheduleSlice.reducer;