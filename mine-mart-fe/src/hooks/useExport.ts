/**
 * Export Receipt hooks với React Query
 * Backend tự động xử lý FIFO + hạn sử dụng
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { exportApi } from '@/api/export.api';
import type { ExportReceiptCreateRequest } from '@/types/export';
import { getErrorMessage } from '@/utils/error';
import { message } from 'antd';

const QUERY_KEYS = {
  all: ['export-receipts'] as const,
  lists: () => [...QUERY_KEYS.all, 'list'] as const,
  details: () => [...QUERY_KEYS.all, 'detail'] as const,
  detail: (id: number) => [...QUERY_KEYS.details(), id] as const,
};

export const useExportReceipts = () => {
  return useQuery({
    queryKey: QUERY_KEYS.lists(),
    queryFn: async () => {
      const response = await exportApi.getAll();
      return response.result || [];
    },
  });
};

export const useExportReceipt = (id: number) => {
  return useQuery({
    queryKey: QUERY_KEYS.detail(id),
    queryFn: async () => {
      const response = await exportApi.getById(id);
      return response.result;
    },
    enabled: !!id,
  });
};

/**
 * Tạo export receipt
 * Backend tự động:
 * - Ưu tiên xuất các lô có hạn sử dụng gần nhất
 * - Nếu 1 lô không đủ → tự động tách sang lô tiếp theo
 * - Tự động tạo nhiều ExportReceiptItem nếu cần
 */
export const useCreateExportReceipt = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: ExportReceiptCreateRequest) => exportApi.create(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.lists() });
      // Invalidate stock report khi có xuất kho
      queryClient.invalidateQueries({ queryKey: ['reports', 'stock'] });
      message.success('Export receipt created successfully');
    },
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

