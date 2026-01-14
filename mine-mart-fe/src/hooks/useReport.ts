/**
 * Report hooks với React Query
 */

import { useQuery } from '@tanstack/react-query';
import { reportApi } from '@/api/report.api';

export const useCurrentStock = () => {
  return useQuery({
    queryKey: ['reports', 'stock'],
    queryFn: async () => {
      const response = await reportApi.getCurrentStock();
      return response.result || [];
    },
  });
};

export const useExpiringItems = (beforeDate?: string) => {
  return useQuery({
    queryKey: ['reports', 'expiring-items', beforeDate],
    queryFn: async () => {
      const response = await reportApi.getExpiringItems(beforeDate);
      return response.result || [];
    },
  });
};

export const useImportExportSummary = (startDate: string, endDate: string) => {
  return useQuery({
    queryKey: ['reports', 'import-export-summary', startDate, endDate],
    queryFn: async () => {
      const response = await reportApi.getImportExportSummary(startDate, endDate);
      return response.result;
    },
    enabled: !!startDate && !!endDate,
  });
};

export const useTopExportingUsers = (startDate: string, endDate: string, limit: number = 10) => {
  return useQuery({
    queryKey: ['reports', 'top-exporting-users', startDate, endDate, limit],
    queryFn: async () => {
      const response = await reportApi.getTopExportingUsers(startDate, endDate, limit);
      return response.result || [];
    },
    enabled: !!startDate && !!endDate,
  });
};

export const useRevenueReport = (
  startDate: string,
  endDate: string,
  groupBy: 'DAY' | 'MONTH' = 'DAY'
) => {
  return useQuery({
    queryKey: ['reports', 'revenue', startDate, endDate, groupBy],
    queryFn: async () => {
      const response = await reportApi.getRevenueReport(startDate, endDate, groupBy);
      return response.result;
    },
    enabled: !!startDate && !!endDate,
  });
};

