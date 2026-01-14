/**
 * Auth Context - Global state cho current user
 * Lưu thông tin user đang đăng nhập
 */

import { createContext, useContext } from 'react';
import type { ReactNode } from 'react';
import type { CurrentUserResponse } from '@/types/auth';

interface AuthContextType {
  user: CurrentUserResponse | null;
  setUser: (user: CurrentUserResponse | null) => void;
  isAdmin: boolean;
  isStaff: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuthContext = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuthContext must be used within AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
  user: CurrentUserResponse | null;
  setUser: (user: CurrentUserResponse | null) => void;
}

export const AuthProvider = ({ children, user, setUser }: AuthProviderProps) => {
  const isAdmin = user?.role === 'Admin';
  const isStaff = user?.role === 'Staff';

  return (
    <AuthContext.Provider value={{ user, setUser, isAdmin, isStaff }}>
      {children}
    </AuthContext.Provider>
  );
};

