/**
 * AI Assistant API
 * Maps to AiAssistantController in backend
 */

import axiosInstance from '@/utils/axios';
import type { ApiResponse } from '@/types/common';
import type { AiAssistantRequest, AiAssistantResponse } from '@/types/ai';

export const aiApi = {
  /**
   * POST /api/ai/assistant
   * Gửi message đến AI Assistant
   * Chỉ STAFF/ADMIN được sử dụng
   */
  processMessage: async (
    request: AiAssistantRequest
  ): Promise<ApiResponse<AiAssistantResponse>> => {
    const response = await axiosInstance.post<ApiResponse<AiAssistantResponse>>(
      '/ai/assistant',
      request
    );
    return response.data;
  },
};


