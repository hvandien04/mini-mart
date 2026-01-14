/**
 * User API
 * Maps to UserController in backend
 */

import axiosInstance from '@/utils/axios';
import type { ApiResponse } from '@/types/common';
import type {
  UserResponse,
  UserCreateRequest,
  UserUpdateRequest,
} from '@/types/user';

export const userApi = {
  /**
   * GET /api/users
   * Lấy tất cả users (Admin only)
   */
  getAll: async (): Promise<ApiResponse<UserResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<UserResponse[]>>('/users');
    return response.data;
  },

  /**
   * GET /api/users/{id}
   * Lấy user theo ID (Admin only)
   */
  getById: async (id: number): Promise<ApiResponse<UserResponse>> => {
    const response = await axiosInstance.get<ApiResponse<UserResponse>>(`/users/${id}`);
    return response.data;
  },

  /**
   * POST /api/users
   * Tạo user mới (Admin only)
   */
  create: async (request: UserCreateRequest): Promise<ApiResponse<UserResponse>> => {
    const response = await axiosInstance.post<ApiResponse<UserResponse>>('/users', request);
    return response.data;
  },

  /**
   * PUT /api/users/{id}
   * Cập nhật user (Admin only)
   */
  update: async (
    id: number,
    request: UserUpdateRequest
  ): Promise<ApiResponse<UserResponse>> => {
    const response = await axiosInstance.put<ApiResponse<UserResponse>>(
      `/users/${id}`,
      request
    );
    return response.data;
  },

  /**
   * PATCH /api/users/{id}/status
   * Cập nhật status (khóa/mở khóa) user (Admin only)
   */
  updateStatus: async (
    id: number,
    isActive: boolean
  ): Promise<ApiResponse<UserResponse>> => {
    const response = await axiosInstance.patch<ApiResponse<UserResponse>>(
      `/users/${id}/status`,
      null,
      { params: { isActive } }
    );
    return response.data;
  },

  /**
   * DELETE /api/users/{id}
   * Xóa user (Admin only)
   */
  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await axiosInstance.delete<ApiResponse<void>>(`/users/${id}`);
    return response.data;
  },
};


