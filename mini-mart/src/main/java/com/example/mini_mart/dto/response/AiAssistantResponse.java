package com.example.mini_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO cho AI Assistant
 * Có thể là Q&A response hoặc Export Preview response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAssistantResponse {
    /**
     * Loại action: Q_AND_A hoặc EXPORT_PREVIEW
     */
    private String action;
    
    /**
     * Câu trả lời từ AI (cho Q&A)
     */
    private String answer;
    
    /**
     * Preview xuất kho (cho EXPORT_PREVIEW)
     */
    private ExportPreviewData exportPreview;
    
    /**
     * Metadata bổ sung
     */
    private String note;
}


