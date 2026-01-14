/**
 * Supplier API
 * Maps to SupplierController in backend
 */

import axiosInstance from '@/utils/axios';
import type { ApiResponse } from '@/types/common';
import type {
  SupplierResponse,
  SupplierCreateRequest,
  SupplierUpdateRequest,
} from '@/types/supplier';

export const supplierApi = {
  /**
   * GET /api/suppliers
   * Lấy tất cả suppliers
   */
  getAll: async (): Promise<ApiResponse<SupplierResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<SupplierResponse[]>>('/suppliers');
    return response.data;
  },

  /**
   * GET /api/suppliers/{id}
   * Lấy supplier theo ID
   */
  getById: async (id: number): Promise<ApiResponse<SupplierResponse>> => {
    const response = await axiosInstance.get<ApiResponse<SupplierResponse>>(`/suppliers/${id}`);
    return response.data;
  },

  /**
   * POST /api/suppliers
   * Tạo supplier mới (Admin only)
   */
  create: async (request: SupplierCreateRequest): Promise<ApiResponse<SupplierResponse>> => {
    const response = await axiosInstance.post<ApiResponse<SupplierResponse>>('/suppliers', request);
    return response.data;
  },

  /**
   * PUT /api/suppliers/{id}
   * Cập nhật supplier (Admin only)
   */
  update: async (
    id: number,
    request: SupplierUpdateRequest
  ): Promise<ApiResponse<SupplierResponse>> => {
    const response = await axiosInstance.put<ApiResponse<SupplierResponse>>(
      `/suppliers/${id}`,
      request
    );
    return response.data;
  },

  /**
   * DELETE /api/suppliers/{id}
   * Xóa supplier (Admin only)
   */
  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await axiosInstance.delete<ApiResponse<void>>(`/suppliers/${id}`);
    return response.data;
  },
};

