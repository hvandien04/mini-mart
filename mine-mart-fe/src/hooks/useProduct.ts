/**
 * Product hooks với React Query
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { productApi } from '@/api/product.api';
import type { ProductCreateRequest, ProductUpdateRequest } from '@/types/product';
import { getErrorMessage } from '@/utils/error';
import { message } from 'antd';

const QUERY_KEYS = {
  all: ['products'] as const,
  lists: () => [...QUERY_KEYS.all, 'list'] as const,
  list: (filters: string) => [...QUERY_KEYS.lists(), { filters }] as const,
  details: () => [...QUERY_KEYS.all, 'detail'] as const,
  detail: (id: number) => [...QUERY_KEYS.details(), id] as const,
  byCategory: (categoryId: number) => [...QUERY_KEYS.lists(), 'category', categoryId] as const,
};

export const useProducts = () => {
  return useQuery({
    queryKey: QUERY_KEYS.lists(),
    queryFn: async () => {
      const response = await productApi.getAll();
      return response.result || [];
    },
  });
};

export const useActiveProducts = () => {
  return useQuery({
    queryKey: [...QUERY_KEYS.lists(), 'active'],
    queryFn: async () => {
      const response = await productApi.getActive();
      return response.result || [];
    },
  });
};

export const useProductsByCategory = (categoryId: number) => {
  return useQuery({
    queryKey: QUERY_KEYS.byCategory(categoryId),
    queryFn: async () => {
      const response = await productApi.getByCategoryId(categoryId);
      return response.result || [];
    },
    enabled: !!categoryId,
  });
};

export const useProductsBySupplier = (supplierId: number) => {
  return useQuery({
    queryKey: [...QUERY_KEYS.lists(), 'supplier', supplierId],
    queryFn: async () => {
      const response = await productApi.getBySupplierId(supplierId);
      return response.result || [];
    },
    enabled: !!supplierId,
  });
};

export const useProduct = (id: number) => {
  return useQuery({
    queryKey: QUERY_KEYS.detail(id),
    queryFn: async () => {
      const response = await productApi.getById(id);
      return response.result;
    },
    enabled: !!id,
  });
};

export const useCreateProduct = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: ProductCreateRequest) => productApi.create(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.lists() });
      message.success('Product created successfully');
    },
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

export const useUpdateProduct = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: ProductUpdateRequest }) =>
      productApi.update(id, request),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.lists() });
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.detail(variables.id) });
      message.success('Product updated successfully');
    },
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

export const useDeleteProduct = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => productApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.lists() });
      message.success('Product deleted successfully');
    },
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

