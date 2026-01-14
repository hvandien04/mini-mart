/**
 * Error handling utilities
 * - Map error code từ backend sang message hiển thị
 */

import type { AxiosError } from 'axios';

/**
 * Error response từ backend có format:
 * {
 *   status: number (error code),
 *   message: string
 * }
 */
export interface ErrorResponse {
  status: number;
  message: string;
}

/**
 * Extract error message từ Axios error
 */
export const getErrorMessage = (error: unknown): string => {
  if (error && typeof error === 'object' && 'isAxiosError' in error) {
    const axiosError = error as AxiosError;
    const errorResponse = axiosError.response?.data as ErrorResponse | undefined;
    if (errorResponse?.message) {
      return errorResponse.message;
    }
    if (axiosError.response?.status === 401) {
      return 'Unauthorized. Please login again.';
    }
    if (axiosError.response?.status === 403) {
      // Kiểm tra URL để xác định loại action
      const url = axiosError.config?.url || '';
      if (url.includes('/products') && axiosError.config?.method === 'put') {
        return 'Bạn không có quyền cập nhật sản phẩm. Chỉ quản trị viên mới có thể thực hiện thao tác này.';
      }
      return 'Bạn không có quyền thực hiện thao tác này.';
    }
    if (axiosError.response?.status === 404) {
      return 'Resource not found.';
    }
    if (axiosError.response?.status === 500) {
      return 'Internal server error. Please try again later.';
    }
    return axiosError.message || 'An error occurred';
  }
  if (error instanceof Error) {
    return error.message;
  }
  return 'An unexpected error occurred';
};

/**
 * Map error code từ backend sang user-friendly message
 * Maps to ErrorCode enum in backend
 */
export const getErrorCodeMessage = (code: number): string => {
  const errorMessages: Record<number, string> = {
    1000: 'You do not have permission',
    1001: 'Unauthenticated. Please login.',
    1002: 'User not found',
    1003: 'Incorrect username or password',
    1012: 'Username already exists',
    1013: 'Username không tồn tại',
    1014: 'Mật khẩu không đúng',
    1015: 'Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.',
    2001: 'Category not found',
    2002: 'Category name already exists',
    3001: 'Product not found',
    3002: 'Product SKU already exists',
    4001: 'Supplier not found',
    5001: 'Customer not found',
    6001: 'Import receipt not found',
    6002: 'Import quantity must be greater than 0',
    6003: 'Sản phẩm không thuộc nhà cung cấp đã chọn',
    7001: 'Export receipt not found',
    7002: 'Insufficient stock',
    7003: 'Export quantity must be greater than 0',
  };
  
  return errorMessages[code] || 'An error occurred';
};

