/**
 * Customer API
 * Maps to CustomerController in backend
 */

import axiosInstance from '@/utils/axios';
import type { ApiResponse } from '@/types/common';
import type {
  CustomerResponse,
  CustomerCreateRequest,
  CustomerUpdateRequest,
} from '@/types/customer';

export const customerApi = {
  /**
   * GET /api/customers
   * Lấy tất cả customers
   */
  getAll: async (): Promise<ApiResponse<CustomerResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<CustomerResponse[]>>('/customers');
    return response.data;
  },

  /**
   * GET /api/customers/{id}
   * Lấy customer theo ID
   */
  getById: async (id: number): Promise<ApiResponse<CustomerResponse>> => {
    const response = await axiosInstance.get<ApiResponse<CustomerResponse>>(`/customers/${id}`);
    return response.data;
  },

  /**
   * POST /api/customers
   * Tạo customer mới (Admin/Staff only)
   */
  create: async (request: CustomerCreateRequest): Promise<ApiResponse<CustomerResponse>> => {
    const response = await axiosInstance.post<ApiResponse<CustomerResponse>>('/customers', request);
    return response.data;
  },

  /**
   * PUT /api/customers/{id}
   * Cập nhật customer (Admin/Staff only)
   */
  update: async (
    id: number,
    request: CustomerUpdateRequest
  ): Promise<ApiResponse<CustomerResponse>> => {
    const response = await axiosInstance.put<ApiResponse<CustomerResponse>>(
      `/customers/${id}`,
      request
    );
    return response.data;
  },

  /**
   * DELETE /api/customers/{id}
   * Xóa customer (Admin only)
   */
  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await axiosInstance.delete<ApiResponse<void>>(`/customers/${id}`);
    return response.data;
  },
};

