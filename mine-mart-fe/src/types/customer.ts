/**
 * Customer types - maps to backend DTOs
 */

export interface CustomerResponse {
  id: number;
  fullName: string;
  phone?: string;
  email?: string;
  address?: string;
  note?: string;
}

export interface CustomerCreateRequest {
  fullName: string;
  phone?: string;
  email?: string;
  address?: string;
  note?: string;
}

export interface CustomerUpdateRequest {
  fullName?: string;
  phone?: string;
  email?: string;
  address?: string;
  note?: string;
}

