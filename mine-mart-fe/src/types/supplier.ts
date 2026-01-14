/**
 * Supplier types - maps to backend DTOs
 */

export interface SupplierResponse {
  id: number;
  name: string;
  phone?: string;
  email?: string;
  address?: string;
  note?: string;
}

export interface SupplierCreateRequest {
  name: string;
  phone?: string;
  email?: string;
  address?: string;
  note?: string;
}

export interface SupplierUpdateRequest {
  name?: string;
  phone?: string;
  email?: string;
  address?: string;
  note?: string;
}

