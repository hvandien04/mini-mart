package com.example.mini_mart.service;

import com.example.mini_mart.dto.response.AiAssistantResponse;

/**
 * Service chính cho AI Assistant
 * Xử lý 2 loại intent:
 * 1. Q_AND_A: Hỏi đáp về dữ liệu kho
 * 2. EXPORT_PREVIEW: Preview xuất kho (không ghi DB)
 */
public interface AiAssistantService {
    /**
     * Xử lý message từ user và trả về response
     * @param message User message (tiếng Việt tự nhiên)
     * @return AI Assistant response
     */
    AiAssistantResponse processMessage(String message);
}


