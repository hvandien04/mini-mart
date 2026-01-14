/**
 * Export Receipt API
 * Maps to ExportReceiptController in backend
 * Backend tự động xử lý FIFO + hạn sử dụng
 * FE chỉ cần gửi productId và quantity, backend tự chia lô
 */

import axiosInstance from '@/utils/axios';
import type { ApiResponse } from '@/types/common';
import type {
  ExportReceiptResponse,
  ExportReceiptCreateRequest,
} from '@/types/export';

export const exportApi = {
  /**
   * GET /api/export-receipts
   * Lấy tất cả export receipts
   */
  getAll: async (): Promise<ApiResponse<ExportReceiptResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<ExportReceiptResponse[]>>(
      '/export-receipts'
    );
    return response.data;
  },

  /**
   * GET /api/export-receipts/{id}
   * Lấy export receipt theo ID
   */
  getById: async (id: number): Promise<ApiResponse<ExportReceiptResponse>> => {
    const response = await axiosInstance.get<ApiResponse<ExportReceiptResponse>>(
      `/export-receipts/${id}`
    );
    return response.data;
  },

  /**
   * POST /api/export-receipts
   * Tạo export receipt mới (Admin/Staff only)
   * 
   * Backend tự động:
   * - Ưu tiên xuất các lô có hạn sử dụng gần nhất (FIFO + expireDate)
   * - Nếu 1 lô không đủ → tự động tách sang lô tiếp theo
   * - Tự động tạo nhiều ExportReceiptItem nếu cần
   * - Validate tồn kho trước khi xuất
   * 
   * FE chỉ cần gửi:
   * - productId
   * - quantity (tổng số lượng cần xuất)
   * - sellingPrice
   */
  create: async (
    request: ExportReceiptCreateRequest
  ): Promise<ApiResponse<ExportReceiptResponse>> => {
    const response = await axiosInstance.post<ApiResponse<ExportReceiptResponse>>(
      '/export-receipts',
      request
    );
    return response.data;
  },
};

