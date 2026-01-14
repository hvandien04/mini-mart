/**
 * Authentication utilities
 * - Lưu/đọc JWT token từ localStorage
 * - Decode JWT để lấy role
 */

const TOKEN_KEY = 'auth_token';
const REFRESH_TOKEN_KEY = 'refresh_token';

export const getToken = (): string | null => {
  return localStorage.getItem(TOKEN_KEY);
};

export const getRefreshToken = (): string | null => {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
};

export const setToken = (token: string, refreshToken: string): void => {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
};

export const removeToken = (): void => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
};

/**
 * Decode JWT token để lấy thông tin user (role, userId)
 * JWT format: header.payload.signature
 */
export const decodeToken = (token: string): { scope?: string; userId?: number } | null => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (error) {
    return null;
  }
};

/**
 * Lấy role từ token
 * Backend trả về role trong claim "scope" (Admin hoặc Staff)
 */
export const getUserRole = (): string | null => {
  const token = getToken();
  if (!token) return null;
  
  const decoded = decodeToken(token);
  return decoded?.scope || null;
};

/**
 * Kiểm tra user có phải Admin không
 */
export const isAdmin = (): boolean => {
  const role = getUserRole();
  return role === 'Admin';
};

/**
 * Kiểm tra user có phải Staff không
 */
export const isStaff = (): boolean => {
  const role = getUserRole();
  return role === 'Staff';
};

/**
 * Kiểm tra user có quyền Admin hoặc Staff không
 */
export const hasAccess = (): boolean => {
  const role = getUserRole();
  return role === 'Admin' || role === 'Staff';
};

