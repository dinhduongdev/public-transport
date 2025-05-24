import axios from 'axios';
import cookie from 'react-cookies';

const BASE_URL = import.meta.env.VITE_API_URL;

const endpoints = {
  register: `${BASE_URL}/api/users`,
  login: `${BASE_URL}/api/login`,
  'google-login': `${BASE_URL}/api/google-login`,
  'current-user': `${BASE_URL}/api/secure/profile`,
  route: `${BASE_URL}/api/routes`,
  routeDetail: (routeId) => `${BASE_URL}/api/routes/${routeId}`,
};

const Apis = {
  post: (url, data, config) => axios.post(url, data, config),
  get: (url, config) => axios.get(url, config), 
};

const authApis = () => ({
  get: (url) =>
    axios.get(url, {
      headers: { Authorization: `Bearer ${cookie.load('token')}` },
    }),
});

export { Apis, authApis, endpoints };