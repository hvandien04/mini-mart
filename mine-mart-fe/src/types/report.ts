/**
 * Report types - maps to backend DTOs
 */

export interface StockReportResponse {
  productId: number;
  productName: string;
  productSku: string;
  totalStock: number; // Tổng tồn kho từ tất cả các lô
}

export interface TopExportingUserResponse {
  userId: number;
  fullName: string;
  totalExportQuantity: number;
  totalExportAmount: number;
}

export interface ExpiringItemResponse {
  importReceiptItemId: number;
  productId: number;
  productName: string;
  productSku: string;
  remainingQuantity: number;
  expireDate: string;
  daysUntilExpiry: number;
}

export interface ImportExportSummaryResponse {
  startDate: string;
  endDate: string;
  totalImportReceipts: number;
  totalExportReceipts: number;
  totalImportQuantity: number;
  totalExportQuantity: number;
  totalImportValue: number;
  totalExportValue: number;
}

export interface RevenueDataPoint {
  date: string;
  dateValue: string;
  revenue: number;
  quantity: number;
}

export interface RevenueReportResponse {
  startDate: string;
  endDate: string;
  groupBy: 'DAY' | 'MONTH';
  dataPoints: RevenueDataPoint[];
  totalRevenue: number;
}

