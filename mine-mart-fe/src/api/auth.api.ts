/**
 * Authentication API
 * Maps to AuthenticationController in backend
 */

import axiosInstance from '@/utils/axios';
import type { ApiResponse } from '@/types/common';
import type {
  AuthenticationRequest,
  AuthenticationResponse,
  RefreshTokenRequest,
  IntrospectRequest,
  IntrospectResponse,
  CurrentUserResponse,
} from '@/types/auth';

export const authApi = {
  /**
   * POST /api/auth/login
   * Login với username và password
   */
  login: async (request: AuthenticationRequest): Promise<ApiResponse<AuthenticationResponse>> => {
    const response = await axiosInstance.post<ApiResponse<AuthenticationResponse>>(
      '/auth/login',
      request
    );
    return response.data;
  },

  /**
   * POST /api/auth/refresh
   * Refresh access token
   */
  refreshToken: async (
    request: RefreshTokenRequest
  ): Promise<ApiResponse<AuthenticationResponse>> => {
    const response = await axiosInstance.post<ApiResponse<AuthenticationResponse>>(
      '/auth/refresh',
      request
    );
    return response.data;
  },

  /**
   * POST /api/auth/introspect
   * Validate token
   */
  introspect: async (request: IntrospectRequest): Promise<ApiResponse<IntrospectResponse>> => {
    const response = await axiosInstance.post<ApiResponse<IntrospectResponse>>(
      '/auth/introspect',
      request
    );
    return response.data;
  },

  /**
   * GET /api/auth/me
   * Get current authenticated user
   */
  getCurrentUser: async (): Promise<ApiResponse<CurrentUserResponse>> => {
    const response = await axiosInstance.get<ApiResponse<CurrentUserResponse>>('/auth/me');
    return response.data;
  },
};

