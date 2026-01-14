/**
 * Import Receipt types - maps to backend DTOs
 */

export interface ImportReceiptItemRequest {
  productId: number;
  quantity: number;
  importPrice: number;
  expireDate?: string; // ISO date string (YYYY-MM-DD)
  note?: string;
}

export interface ImportReceiptCreateRequest {
  supplierId: number;
  importDate: string; // ISO datetime string
  items: ImportReceiptItemRequest[];
  note?: string;
}

export interface ImportReceiptItemResponse {
  id: number;
  productId: number;
  productName: string;
  productSku: string;
  quantity: number;
  remainingQuantity: number;
  importPrice: number;
  expireDate?: string;
  note?: string;
}

export interface ImportReceiptResponse {
  id: number;
  userId: number;
  userName: string;
  supplierId: number;
  supplierName: string;
  importDate: string;
  createdAt: string;
  note?: string;
  items: ImportReceiptItemResponse[];
}

