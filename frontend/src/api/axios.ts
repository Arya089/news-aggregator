import axios from 'axios';

const API = axios.create({
  baseURL: process.env.REACT_APP_API_URL || '',
  headers: { 'ngrok-skip-browser-warning': 'true' },
});

API.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  const isAuthRoute = config.url?.includes('/api/auth/login') || config.url?.includes('/api/auth/register');
  if (token && !isAuthRoute) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

API.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && window.location.pathname !== '/login') {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default API;