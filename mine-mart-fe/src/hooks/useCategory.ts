/**
 * Category hooks với React Query
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { categoryApi } from '@/api/category.api';
import type { CategoryCreateRequest, CategoryUpdateRequest } from '@/types/category';
import { getErrorMessage } from '@/utils/error';
import { message } from 'antd';

const QUERY_KEYS = {
  all: ['categories'] as const,
  lists: () => [...QUERY_KEYS.all, 'list'] as const,
  list: (filters: string) => [...QUERY_KEYS.lists(), { filters }] as const,
  details: () => [...QUERY_KEYS.all, 'detail'] as const,
  detail: (id: number) => [...QUERY_KEYS.details(), id] as const,
};

export const useCategories = () => {
  return useQuery({
    queryKey: QUERY_KEYS.lists(),
    queryFn: async () => {
      const response = await categoryApi.getAll();
      return response.result || [];
    },
  });
};

export const useActiveCategories = () => {
  return useQuery({
    queryKey: [...QUERY_KEYS.lists(), 'active'],
    queryFn: async () => {
      const response = await categoryApi.getActive();
      return response.result || [];
    },
  });
};

export const useCategory = (id: number) => {
  return useQuery({
    queryKey: QUERY_KEYS.detail(id),
    queryFn: async () => {
      const response = await categoryApi.getById(id);
      return response.result;
    },
    enabled: !!id,
  });
};

export const useCreateCategory = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: CategoryCreateRequest) => categoryApi.create(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.lists() });
      message.success('Category created successfully');
    },
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

export const useUpdateCategory = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: CategoryUpdateRequest }) =>
      categoryApi.update(id, request),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.lists() });
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.detail(variables.id) });
      message.success('Category updated successfully');
    },
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

export const useDeleteCategory = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => categoryApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.lists() });
      message.success('Category deleted successfully');
    },
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

