/**
 * Main App Component
 * Setup React Query và Router
 */

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { RouterProvider } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import viVN from 'antd/locale/vi_VN';
import { router } from './router';
import { AuthProvider } from './contexts/AuthContext';
import { useCurrentUser } from './hooks/useAuth';
import { useState, useEffect } from 'react';
import type { CurrentUserResponse } from './types/auth';
import { getToken } from './utils/auth';
import dayjs from 'dayjs';
import 'dayjs/locale/vi';

// Setup dayjs locale
dayjs.locale('vi');

// Create React Query client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

// Component để wrap AuthProvider với current user logic
const AppWithAuth = () => {
  const [user, setUser] = useState<CurrentUserResponse | null>(null);
  const token = getToken();
  const { data: currentUser, isError, isLoading, refetch } = useCurrentUser();

  // Lắng nghe event userFetched từ useLogin
  useEffect(() => {
    const handleUserFetched = (event: Event) => {
      const customEvent = event as CustomEvent<CurrentUserResponse>;
      if (customEvent.detail) {
        setUser(customEvent.detail);
      }
    };

    window.addEventListener('userFetched', handleUserFetched);
    return () => {
      window.removeEventListener('userFetched', handleUserFetched);
    };
  }, []);

  // Khi có token nhưng chưa có user, refetch
  useEffect(() => {
    if (token && !currentUser && !isLoading) {
      // Đợi một chút để đảm bảo axios interceptor đã set token
      const timer = setTimeout(() => {
        refetch();
      }, 100);
      return () => clearTimeout(timer);
    }
  }, [token, currentUser, isLoading, refetch]);

  useEffect(() => {
    if (currentUser) {
      setUser(currentUser);
    } else if (isError || (!isLoading && !currentUser && !token)) {
      // Nếu lỗi hoặc không có token thì clear user
      setUser(null);
    }
  }, [currentUser, isError, isLoading, token]);

  return (
    <AuthProvider user={user} setUser={setUser}>
      <RouterProvider router={router} key={user?.id || 'no-user'} />
    </AuthProvider>
  );
};

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ConfigProvider locale={viVN}>
        <AppWithAuth />
      </ConfigProvider>
    </QueryClientProvider>
  );
}

export default App;
