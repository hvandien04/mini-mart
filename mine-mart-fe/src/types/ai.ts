/**
 * AI Assistant Types
 */

export interface AiAssistantRequest {
  message: string;
}

export interface ExportPreviewData {
  customer: {
    id: number;
    name: string;
  };
  // Deprecated: Dùng items thay vì product/quantity/unitPrice/totalPrice/stockStatus
  product?: {
    id: number;
    name: string;
    sku: string;
  };
  quantity?: number;
  unitPrice?: number;
  totalPrice?: number;
  stockStatus?: 'ENOUGH' | 'INSUFFICIENT';
  // Danh sách sản phẩm cần xuất
  items?: ExportPreviewItem[];
  // Tổng tiền của tất cả sản phẩm
  totalAmount?: number;
  // Trạng thái tồn kho tổng thể
  overallStockStatus?: 'ENOUGH' | 'INSUFFICIENT';
  note: string;
}

export interface ExportPreviewItem {
  product: {
    id: number;
    name: string;
    sku: string;
  };
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  stockStatus: 'ENOUGH' | 'INSUFFICIENT';
  note: string;
}

export interface AiAssistantResponse {
  action: 'Q_AND_A' | 'EXPORT_PREVIEW';
  answer: string;
  exportPreview?: ExportPreviewData;
  note?: string;
}


