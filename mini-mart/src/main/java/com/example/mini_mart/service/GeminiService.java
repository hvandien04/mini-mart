package com.example.mini_mart.service;

import com.example.mini_mart.dto.response.AiIntentResponse;

import java.util.List;
import java.util.Map;

/**
 * Service để tương tác với Google Gemini API
 * - Generate embeddings cho semantic search
 * - Chat completion để phân tích intent và trả lời
 */
public interface GeminiService {
    /**
     * Tạo embedding vector từ text
     * @param text Text cần embed
     * @return Embedding vector
     */
    List<Float> generateEmbedding(String text);
    
    /**
     * Chat completion với Gemini
     * @param prompt System prompt
     * @param userMessage User message
     * @param context Context data từ DB
     * @return Response từ Gemini
     */
    String chatCompletion(String prompt, String userMessage, Map<String, Object> context);
    
    /**
     * Phân tích intent từ user message và trả về JSON
     * AI chỉ làm nhiệm vụ hiểu ngôn ngữ tự nhiên, không dùng regex/if-else
     * @param message User message (tiếng Việt tự nhiên)
     * @return Intent response dạng JSON
     */
    AiIntentResponse parseIntent(String message);
    
    /**
     * @deprecated Dùng parseIntent thay vì analyzeIntent
     */
    @Deprecated
    String analyzeIntent(String message);
}


