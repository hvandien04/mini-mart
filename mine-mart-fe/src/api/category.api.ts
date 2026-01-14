/**
 * Category API
 * Maps to CategoryController in backend
 */

import axiosInstance from '@/utils/axios';
import type { ApiResponse } from '@/types/common';
import type {
  CategoryResponse,
  CategoryCreateRequest,
  CategoryUpdateRequest,
} from '@/types/category';

export const categoryApi = {
  /**
   * GET /api/categories
   * Lấy tất cả categories
   */
  getAll: async (): Promise<ApiResponse<CategoryResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<CategoryResponse[]>>('/categories');
    return response.data;
  },

  /**
   * GET /api/categories/active
   * Lấy tất cả active categories
   */
  getActive: async (): Promise<ApiResponse<CategoryResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<CategoryResponse[]>>('/categories/active');
    return response.data;
  },

  /**
   * GET /api/categories/{id}
   * Lấy category theo ID
   */
  getById: async (id: number): Promise<ApiResponse<CategoryResponse>> => {
    const response = await axiosInstance.get<ApiResponse<CategoryResponse>>(`/categories/${id}`);
    return response.data;
  },

  /**
   * POST /api/categories
   * Tạo category mới (Admin only)
   */
  create: async (request: CategoryCreateRequest): Promise<ApiResponse<CategoryResponse>> => {
    const response = await axiosInstance.post<ApiResponse<CategoryResponse>>(
      '/categories',
      request
    );
    return response.data;
  },

  /**
   * PUT /api/categories/{id}
   * Cập nhật category (Admin only)
   */
  update: async (
    id: number,
    request: CategoryUpdateRequest
  ): Promise<ApiResponse<CategoryResponse>> => {
    const response = await axiosInstance.put<ApiResponse<CategoryResponse>>(
      `/categories/${id}`,
      request
    );
    return response.data;
  },

  /**
   * DELETE /api/categories/{id}
   * Xóa category (Admin only)
   */
  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await axiosInstance.delete<ApiResponse<void>>(`/categories/${id}`);
    return response.data;
  },
};

