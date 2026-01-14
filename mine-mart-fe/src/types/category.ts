/**
 * Category types - maps to backend DTOs
 */

export interface CategoryResponse {
  id: number;
  name: string;
  description?: string;
  isActive: boolean;
}

export interface CategoryCreateRequest {
  name: string;
  description?: string;
}

export interface CategoryUpdateRequest {
  name?: string;
  description?: string;
  isActive?: boolean;
}

