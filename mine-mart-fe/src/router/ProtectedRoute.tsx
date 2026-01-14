/**
 * Protected Route Component
 * Kiểm tra authentication và authorization trước khi cho phép truy cập
 */

import type { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { getToken } from '@/utils/auth';
import { useAuthContext } from '@/contexts/AuthContext';

interface ProtectedRouteProps {
  children: ReactNode;
  requireAdmin?: boolean;
}

export const ProtectedRoute = ({ children, requireAdmin = false }: ProtectedRouteProps) => {
  const token = getToken();
  const { isAdmin } = useAuthContext();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // Nếu route yêu cầu Admin nhưng user không phải Admin
  if (requireAdmin && !isAdmin) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};

