/**
 * Common API Response wrapper from backend
 * Maps to ApiResponse<T> in backend
 */
export interface ApiResponse<T> {
  status: number;
  timestamp: string;
  message: string;
  result: T;
}

