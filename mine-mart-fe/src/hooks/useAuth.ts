/**
 * Authentication hooks với React Query
 */

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { authApi } from '@/api/auth.api';
import { setToken, removeToken, getToken } from '@/utils/auth';
import { getErrorMessage } from '@/utils/error';
import { message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useAuthContext } from '@/contexts/AuthContext';

export const useLogin = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: authApi.login,
    onSuccess: async (response) => {
      if (response.result?.token && response.result?.refreshToken) {
        setToken(response.result.token, response.result.refreshToken);
        // Clear cache currentUser
        queryClient.removeQueries({ queryKey: ['currentUser'] });
        // Fetch user ngay lập tức và set vào cache
        try {
          const userResponse = await authApi.getCurrentUser();
          if (userResponse.result) {
            // Set vào query cache để App.tsx có thể nhận được
            queryClient.setQueryData(['currentUser'], userResponse.result);
            // Trigger một custom event để App.tsx biết user đã được fetch
            window.dispatchEvent(new CustomEvent('userFetched', { detail: userResponse.result }));
          }
        } catch (error) {
          console.error('Failed to fetch current user after login:', error);
        }
        message.success('Login successful');
        navigate('/');
      }
    },
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

export const useLogout = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const { setUser } = useAuthContext();

  return () => {
    // Clear user state ngay lập tức
    setUser(null);
    // Clear token
    removeToken();
    // Clear all queries (including currentUser)
    queryClient.clear();
    // Remove all cached data
    queryClient.removeQueries();
    // Reset query cache
    queryClient.resetQueries();
    // Navigate to login
    navigate('/login');
  };
};

/**
 * Hook để lấy current user
 * Tự động fetch khi có token
 */
export const useCurrentUser = () => {
  const token = getToken();
  
  return useQuery({
    queryKey: ['currentUser'],
    queryFn: async () => {
      const response = await authApi.getCurrentUser();
      return response.result;
    },
    enabled: !!token, // Chỉ fetch khi có token
    retry: false,
    staleTime: 0, // Luôn refetch khi cần
    refetchOnMount: true, // Refetch khi component mount
  });
};

