package com.example.mini_mart.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO cho AI Assistant
 * User gửi câu hỏi/lệnh bằng tiếng Việt tự nhiên
 */
@Data
public class AiAssistantRequest {
    @NotBlank(message = "Message cannot be blank")
    private String message;
}


