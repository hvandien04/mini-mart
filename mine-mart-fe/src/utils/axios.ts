/**
 * Axios instance với interceptors
 * - Tự động gắn JWT token vào header
 * - Tự động logout khi 401
 */

import axios, { AxiosError } from 'axios';
import type { InternalAxiosRequestConfig } from 'axios';
import { getToken, removeToken } from './auth';

// Lưu ý: getToken() trả về từ localStorage với key 'auth_token'
// Nếu backend dùng key khác, cần cập nhật

const axiosInstance = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor: gắn token vào header
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken();
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: unknown) => {
    return Promise.reject(error);
  }
);

// Response interceptor: xử lý 401 (unauthorized)
axiosInstance.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (error && typeof error === 'object' && 'isAxiosError' in error) {
      const axiosError = error as AxiosError;
      if (axiosError.response?.status === 401) {
        // Unauthorized - xóa token và redirect về login
        removeToken();
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;

