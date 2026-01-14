/**
 * Product types - maps to backend DTOs
 */

export interface ProductResponse {
  id: number;
  categoryId: number;
  categoryName?: string;
  supplierId: number;
  supplierName?: string;
  sku: string;
  name: string;
  brand?: string;
  description?: string;
  imageUrl?: string;
  unit?: string;
  sellingPrice: number;
  isActive: boolean;
  createdAt: string;
}

export interface ProductCreateRequest {
  categoryId: number;
  supplierId: number;
  sku: string;
  name: string;
  brand?: string;
  description?: string;
  imageUrl?: string;
  unit?: string;
  sellingPrice: number;
}

export interface ProductUpdateRequest {
  categoryId?: number;
  supplierId?: number;
  sku?: string;
  name?: string;
  brand?: string;
  description?: string;
  imageUrl?: string;
  unit?: string;
  sellingPrice?: number;
  isActive?: boolean;
}

