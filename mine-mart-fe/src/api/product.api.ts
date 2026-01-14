/**
 * Product API
 * Maps to ProductController in backend
 */

import axiosInstance from '@/utils/axios';
import type { ApiResponse } from '@/types/common';
import type {
  ProductResponse,
  ProductCreateRequest,
  ProductUpdateRequest,
} from '@/types/product';

export const productApi = {
  /**
   * GET /api/products
   * Lấy tất cả products
   */
  getAll: async (): Promise<ApiResponse<ProductResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<ProductResponse[]>>('/products');
    return response.data;
  },

  /**
   * GET /api/products/active
   * Lấy tất cả active products
   */
  getActive: async (): Promise<ApiResponse<ProductResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<ProductResponse[]>>('/products/active');
    return response.data;
  },

  /**
   * GET /api/products/category/{categoryId}
   * Lấy products theo category
   */
  getByCategoryId: async (categoryId: number): Promise<ApiResponse<ProductResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<ProductResponse[]>>(
      `/products/category/${categoryId}`
    );
    return response.data;
  },

  /**
   * GET /api/products/{id}
   * Lấy product theo ID
   */
  getById: async (id: number): Promise<ApiResponse<ProductResponse>> => {
    const response = await axiosInstance.get<ApiResponse<ProductResponse>>(`/products/${id}`);
    return response.data;
  },

  /**
   * POST /api/products
   * Tạo product mới (Admin only)
   */
  create: async (request: ProductCreateRequest): Promise<ApiResponse<ProductResponse>> => {
    const response = await axiosInstance.post<ApiResponse<ProductResponse>>('/products', request);
    return response.data;
  },

  /**
   * PUT /api/products/{id}
   * Cập nhật product (Admin only)
   */
  update: async (
    id: number,
    request: ProductUpdateRequest
  ): Promise<ApiResponse<ProductResponse>> => {
    const response = await axiosInstance.put<ApiResponse<ProductResponse>>(
      `/products/${id}`,
      request
    );
    return response.data;
  },

  /**
   * DELETE /api/products/{id}
   * Xóa product (Admin only)
   */
  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await axiosInstance.delete<ApiResponse<void>>(`/products/${id}`);
    return response.data;
  },

  /**
   * GET /api/products/supplier/{supplierId}
   * Lấy active products theo supplier
   * Dùng để filter product khi tạo phiếu nhập kho
   */
  getBySupplierId: async (supplierId: number): Promise<ApiResponse<ProductResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<ProductResponse[]>>(
      `/products/supplier/${supplierId}`
    );
    return response.data;
  },
};

