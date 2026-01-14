/**
 * Report API
 * Maps to ReportController in backend
 */

import axiosInstance from '@/utils/axios';
import type { ApiResponse } from '@/types/common';
import type {
  StockReportResponse,
  ExpiringItemResponse,
  ImportExportSummaryResponse,
  TopExportingUserResponse,
  RevenueReportResponse,
} from '@/types/report';

export const reportApi = {
  /**
   * GET /api/reports/stock
   * Lấy tồn kho hiện tại (group theo product)
   * Tổng tồn = tổng remainingQuantity từ tất cả các lô
   */
  getCurrentStock: async (): Promise<ApiResponse<StockReportResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<StockReportResponse[]>>(
      '/reports/stock'
    );
    return response.data;
  },

  /**
   * GET /api/reports/expiring-items
   * Lấy danh sách hàng sắp hết hạn
   * @param beforeDate - Ngày kiểm tra (ISO format: yyyy-MM-dd). Default: 30 ngày từ bây giờ
   */
  getExpiringItems: async (
    beforeDate?: string
  ): Promise<ApiResponse<ExpiringItemResponse[]>> => {
    const params = beforeDate ? { beforeDate } : {};
    const response = await axiosInstance.get<ApiResponse<ExpiringItemResponse[]>>(
      '/reports/expiring-items',
      { params }
    );
    return response.data;
  },

  /**
   * GET /api/reports/import-export-summary
   * Lấy tổng nhập - xuất theo khoảng thời gian
   * @param startDate - Ngày bắt đầu (ISO format: yyyy-MM-ddTHH:mm:ss)
   * @param endDate - Ngày kết thúc (ISO format: yyyy-MM-ddTHH:mm:ss)
   */
  getImportExportSummary: async (
    startDate: string,
    endDate: string
  ): Promise<ApiResponse<ImportExportSummaryResponse>> => {
    const response = await axiosInstance.get<ApiResponse<ImportExportSummaryResponse>>(
      '/reports/import-export-summary',
      {
        params: { startDate, endDate },
      }
    );
    return response.data;
  },

  /**
   * GET /api/reports/top-exporting-users
   * Lấy top nhân viên xuất kho nhiều nhất
   * @param startDate - Ngày bắt đầu (ISO format: yyyy-MM-ddTHH:mm:ss)
   * @param endDate - Ngày kết thúc (ISO format: yyyy-MM-ddTHH:mm:ss)
   * @param limit - Số lượng top nhân viên (default: 10)
   */
  getTopExportingUsers: async (
    startDate: string,
    endDate: string,
    limit: number = 10
  ): Promise<ApiResponse<TopExportingUserResponse[]>> => {
    const response = await axiosInstance.get<ApiResponse<TopExportingUserResponse[]>>(
      '/reports/top-exporting-users',
      {
        params: { startDate, endDate, limit },
      }
    );
    return response.data;
  },

  /**
   * GET /api/reports/revenue
   * Lấy báo cáo doanh thu theo ngày/tháng
   * Doanh thu = tổng (quantity * sellingPrice) từ ExportReceiptItem
   * @param startDate - Ngày bắt đầu (ISO format: yyyy-MM-ddTHH:mm:ss)
   * @param endDate - Ngày kết thúc (ISO format: yyyy-MM-ddTHH:mm:ss)
   * @param groupBy - Group by: "DAY" hoặc "MONTH" (default: "DAY")
   */
  getRevenueReport: async (
    startDate: string,
    endDate: string,
    groupBy: 'DAY' | 'MONTH' = 'DAY'
  ): Promise<ApiResponse<RevenueReportResponse>> => {
    const response = await axiosInstance.get<ApiResponse<RevenueReportResponse>>(
      '/reports/revenue',
      {
        params: { startDate, endDate, groupBy },
      }
    );
    return response.data;
  },
};

