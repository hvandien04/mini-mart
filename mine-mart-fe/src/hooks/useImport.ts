/**
 * Import Receipt hooks với React Query
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { importApi } from '@/api/import.api';
import type { ImportReceiptCreateRequest } from '@/types/import';
import { getErrorMessage } from '@/utils/error';
import { message } from 'antd';

const QUERY_KEYS = {
  all: ['import-receipts'] as const,
  lists: () => [...QUERY_KEYS.all, 'list'] as const,
  details: () => [...QUERY_KEYS.all, 'detail'] as const,
  detail: (id: number) => [...QUERY_KEYS.details(), id] as const,
};

export const useImportReceipts = () => {
  return useQuery({
    queryKey: QUERY_KEYS.lists(),
    queryFn: async () => {
      const response = await importApi.getAll();
      return response.result || [];
    },
  });
};

export const useImportReceipt = (id: number) => {
  return useQuery({
    queryKey: QUERY_KEYS.detail(id),
    queryFn: async () => {
      const response = await importApi.getById(id);
      return response.result;
    },
    enabled: !!id,
  });
};

export const useImportReceiptItemsWithFilter = (id: number, hasRemaining?: boolean) => {
  return useQuery({
    queryKey: [...QUERY_KEYS.detail(id), 'items', hasRemaining],
    queryFn: async () => {
      const response = await importApi.getItemsWithFilter(id, hasRemaining);
      return response.result;
    },
    enabled: !!id,
  });
};

export const useCreateImportReceipt = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: ImportReceiptCreateRequest) => importApi.create(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.lists() });
      // Invalidate stock report khi có nhập kho mới
      queryClient.invalidateQueries({ queryKey: ['reports', 'stock'] });
      message.success('Import receipt created successfully');
    },
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

