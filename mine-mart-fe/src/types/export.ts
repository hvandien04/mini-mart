/**
 * Export Receipt types - maps to backend DTOs
 * Note: FE chỉ gửi productId và quantity, backend tự xử lý FIFO + hạn sử dụng
 */

export interface ExportReceiptItemRequest {
  productId: number;
  quantity: number;
  sellingPrice: number;
  note?: string;
}

export interface ExportReceiptCreateRequest {
  customerId: number;
  exportDate: string; // ISO datetime string
  items: ExportReceiptItemRequest[];
  note?: string;
}

export interface ExportReceiptItemResponse {
  id: number;
  productId: number;
  productName: string;
  productSku: string;
  importReceiptItemId: number; // Lô hàng đã được xuất
  quantity: number;
  sellingPrice: number;
  note?: string;
}

export interface ExportReceiptResponse {
  id: number;
  userId: number;
  userName: string;
  customerId: number;
  customerName: string;
  exportDate: string;
  createdAt: string;
  note?: string;
  items: ExportReceiptItemResponse[]; // Backend tự động tạo nhiều items nếu cần (FIFO)
}

