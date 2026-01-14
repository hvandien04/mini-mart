package com.example.mini_mart.controller;

import com.example.mini_mart.dto.request.AiAssistantRequest;
import com.example.mini_mart.dto.response.AiAssistantResponse;
import com.example.mini_mart.dto.response.ApiResponse;
import com.example.mini_mart.service.AiAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller cho AI Assistant
 * 
 * QUAN TRỌNG:
 * - AI chỉ đọc và đề xuất, KHÔNG được ghi DB
 * - Chỉ STAFF và ADMIN mới được sử dụng
 * - Rate limit nên được áp dụng ở production
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Assistant", description = "AI Assistant APIs for inventory Q&A and export preview")
public class AiAssistantController {
    
    private final AiAssistantService aiAssistantService;
    
    @PostMapping("/assistant")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    @Operation(
        summary = "AI Assistant",
        description = "Xử lý câu hỏi/lệnh từ user. Hỗ trợ Q&A về kho và preview xuất kho. Chỉ STAFF/ADMIN được sử dụng.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "AI response retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ApiResponse<AiAssistantResponse> processMessage(@Valid @RequestBody AiAssistantRequest request) {
        log.info("AI Assistant request: {}", request.getMessage());
        
        AiAssistantResponse response = aiAssistantService.processMessage(request.getMessage());
        
        return ApiResponse.<AiAssistantResponse>builder()
                .status(HttpStatus.OK.value())
                .message("AI response retrieved successfully")
                .result(response)
                .build();
    }
}


