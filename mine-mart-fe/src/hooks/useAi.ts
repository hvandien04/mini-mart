/**
 * AI Assistant hooks với React Query
 */

import { useMutation } from '@tanstack/react-query';
import { aiApi } from '@/api/ai.api';
import type { AiAssistantRequest } from '@/types/ai';
import { getErrorMessage } from '@/utils/error';
import { message } from 'antd';

export const useAiAssistant = () => {
  return useMutation({
    mutationFn: (request: AiAssistantRequest) => aiApi.processMessage(request),
    onError: (error) => {
      message.error(getErrorMessage(error));
    },
  });
};

