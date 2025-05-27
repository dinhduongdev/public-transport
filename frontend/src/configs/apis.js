import axios from "axios";
import cookie from "react-cookies";

const BASE_URL = import.meta.env.VITE_API_URL;

const endpoints = {
  register: `${BASE_URL}/api/register/`,
  login: `${BASE_URL}/api/login`,
  "send-verification-code": `${BASE_URL}/api/send-verification-code`,
  "google-login": `${BASE_URL}/api/google-login`,
  "current-user": `${BASE_URL}/api/secure/profile`,
  route: `${BASE_URL}/api/routes`,
  routeDetail: (routeId) => `${BASE_URL}/api/routes/${routeId}`,
  ratingSummary: (routeId) =>
    `${BASE_URL}/api/ratings/summary?routeId=${routeId}`,
  submitRating: `${BASE_URL}/api/ratings`,
  favoritesByUser: (userId, targetType) =>
    `${BASE_URL}/api/favorites/user/${userId}?targetType=${targetType}`,
  favoriteById: (favoriteId) => `${BASE_URL}/api/favorites/${favoriteId}`,
  favorites: `${BASE_URL}/api/favorites`,
  //notifications
  notificationsByUser: (userId) =>
    `${BASE_URL}/api/notifications/user/${userId}`, // Thêm endpoint
  notificationById: (notificationId) =>
    `${BASE_URL}/api/notifications/${notificationId}`,
};

const Apis = {
  post: (url, data, config) => axios.post(url, data, config),
  get: (url, config) => axios.get(url, config),
};

const authApis = () => ({
  get: (url) =>
    axios.get(url, {
      headers: { Authorization: `Bearer ${cookie.load("token")}` },
    }),
  post: (url, data) =>
    axios.post(url, data, {
      headers: { Authorization: `Bearer ${cookie.load("token")}` },
    }),
  delete: (url) =>
    axios.delete(url, {
      headers: { Authorization: `Bearer ${cookie.load("token")}` },
    }),
  patch: (url, data) =>
    axios.patch(url, data, {
      headers: { Authorization: `Bearer ${cookie.load("token")}` },
    }),
});

export { Apis, authApis, endpoints };
