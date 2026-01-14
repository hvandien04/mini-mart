/**
 * User types - maps to backend DTOs
 */

export interface UserResponse {
  id: number;
  fullName: string;
  username: string;
  email?: string;
  phone?: string;
  address?: string;
  role: string;
  isActive: boolean;
  createdAt: string;
}

export interface UserCreateRequest {
  fullName: string;
  username: string;
  password: string;
  email?: string;
  phone?: string;
  role: string;
}

export interface UserUpdateRequest {
  fullName?: string;
  email?: string;
  phone?: string;
  address?: string;
  role?: string;
  isActive?: boolean;
}


