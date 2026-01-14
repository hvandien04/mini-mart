/**
 * Supplier hooks với React Query
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { supplierApi } from '@/api/supplier.api';
import type { SupplierCreateRequest, SupplierUpdateRequest } from '@/types/supplier';
import { getErrorMessage } from '@/utils/error';
import { message } from 'antd';

const QUERY_KEYS = {
  all: ['suppliers'] as const,
  lists: () => [...QUERY_KEYS.all, 'list'] as const,
  details: () => [...QUERY_KEYS.all, 'detail'] as const,
  detail: (id: number) => [...QUERY_KEYS.details(), id] as const,
};

export const useSuppliers = () => {
  return useQuery({
    queryKey: QUERY_KEYS.lists(),
    queryFn: async () => {
      const response = await supplierApi.getAll();
      return response.result || [];
    },
  });
};

export const useSupplier = (id: number) => {
  return useQuery({
    queryKey: QUERY_KEYS.detail(id),
    queryFn: async () => {
      const response = await supplierApi.getById(id);
      return response.result;
    },
    enabled: !!id,
  });
};

export const useCreateSupplier = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: SupplierCreateRequest) => supplierApi.create(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.lists() });
      message.success('Supplier created successfully');
    },
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

export const useUpdateSupplier = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: SupplierUpdateRequest }) =>
      supplierApi.update(id, request),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.lists() });
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.detail(variables.id) });
      message.success('Supplier updated successfully');
    },
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

export const useDeleteSupplier = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => supplierApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.lists() });
      message.success('Supplier deleted successfully');
    },
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

