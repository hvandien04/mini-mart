/**
 * Import Receipt API
 * Maps to ImportReceiptController in backend
 * Mỗi item trong import receipt tạo thành một lô hàng (batch)
 */

import axiosInstance from '@/utils/axios';
import type { ApiResponse } from '@/types/common';
import type {
  ImportReceiptResponse,
  ImportReceiptCreateRequest,
} from '@/types/import';

export const importApi = {
  /**
   * GET /api/import-receipts
   * Lấy tất cả import receipts
   */
  getAll: async (): Promise<ApiResponse<ImportReceiptResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<ImportReceiptResponse[]>>(
      '/import-receipts'
    );
    return response.data;
  },

  /**
   * GET /api/import-receipts/{id}
   * Lấy import receipt theo ID
   */
  getById: async (id: number): Promise<ApiResponse<ImportReceiptResponse>> => {
    const response = await axiosInstance.get<ApiResponse<ImportReceiptResponse>>(
      `/import-receipts/${id}`
    );
    return response.data;
  },

  /**
   * POST /api/import-receipts
   * Tạo import receipt mới (Admin/Staff only)
   * Mỗi item trong items sẽ tạo thành một lô hàng với:
   * - quantity: số lượng nhập
   * - remainingQuantity: số lượng còn lại (ban đầu = quantity)
   * - expireDate: hạn sử dụng
   */
  create: async (
    request: ImportReceiptCreateRequest
  ): Promise<ApiResponse<ImportReceiptResponse>> => {
    const response = await axiosInstance.post<ApiResponse<ImportReceiptResponse>>(
      '/import-receipts',
      request
    );
    return response.data;
  },

  /**
   * GET /api/import-receipts/{id}/items
   * Lấy import receipt items với filter theo trạng thái
   * @param id Import receipt ID
   * @param hasRemaining true = còn hàng, false = hết hàng, undefined = tất cả
   */
  getItemsWithFilter: async (
    id: number,
    hasRemaining?: boolean
  ): Promise<ApiResponse<ImportReceiptResponse>> => {
    const params = hasRemaining !== undefined ? { hasRemaining } : {};
    const response = await axiosInstance.get<ApiResponse<ImportReceiptResponse>>(
      `/import-receipts/${id}/items`,
      { params }
    );
    return response.data;
  },
};

