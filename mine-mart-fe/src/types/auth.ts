/**
 * Authentication types - maps to backend DTOs
 */

export interface AuthenticationRequest {
  username: string;
  password: string;
}

export interface AuthenticationResponse {
  authenticated: boolean;
  token: string;
  refreshToken: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface IntrospectRequest {
  token: string;
}

export interface IntrospectResponse {
  valid: boolean;
}

export interface CurrentUserResponse {
  id: number;
  fullName: string;
  username: string;
  role: string;
}

