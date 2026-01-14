package com.example.mini_mart.controller;

import com.example.mini_mart.dto.response.ApiResponse;
import com.example.mini_mart.service.GeminiService;
import com.example.mini_mart.service.VectorSyncService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller để quản lý vector sync vào Qdrant
 * Chỉ ADMIN mới được trigger sync
 */
@Slf4j
@RestController
@RequestMapping("/api/vector-sync")
@RequiredArgsConstructor
@Tag(name = "Vector Sync", description = "API để sync dữ liệu vào Qdrant vector database")
public class VectorSyncController {
    
    private final VectorSyncService vectorSyncService;
    private final GeminiService geminiService;
    
    /**
     * Sync tất cả products vào Qdrant
     * LƯU Ý: Sẽ tốn Gemini API quota (~1 request/product)
     */
    @PostMapping("/products")
    @PreAuthorize("hasAuthority('Admin')")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<String> syncAllProducts() {
        log.info("Manual sync all products triggered by admin");
        try {
            vectorSyncService.syncAllProducts();
            return ApiResponse.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Products sync completed successfully")
                    .result("Sync completed. Check logs for details.")
                    .build();
        } catch (Exception e) {
            log.error("Error syncing products", e);
            return ApiResponse.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to sync products: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }
    
    /**
     * Sync tất cả customers vào Qdrant
     * LƯU Ý: Sẽ tốn Gemini API quota (~1 request/customer)
     */
    @PostMapping("/customers")
    @PreAuthorize("hasAuthority('Admin')")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<String> syncAllCustomers() {
        log.info("Manual sync all customers triggered by admin");
        try {
            vectorSyncService.syncAllCustomers();
            return ApiResponse.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Customers sync completed successfully")
                    .result("Sync completed. Check logs for details.")
                    .build();
        } catch (Exception e) {
            log.error("Error syncing customers", e);
            return ApiResponse.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to sync customers: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }
    
    /**
     * Sync một product cụ thể vào Qdrant
     */
    @PostMapping("/products/{productId}")
    @PreAuthorize("hasAuthority('Admin')")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<String> syncProduct(@PathVariable Integer productId) {
        log.info("Manual sync product {} triggered by admin", productId);
        try {
            vectorSyncService.syncProduct(productId);
            return ApiResponse.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Product sync completed successfully")
                    .result("Product " + productId + " synced to Qdrant")
                    .build();
        } catch (Exception e) {
            log.error("Error syncing product {}", productId, e);
            return ApiResponse.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to sync product: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }
    
    /**
     * Sync một customer cụ thể vào Qdrant
     */
    @PostMapping("/customers/{customerId}")
    @PreAuthorize("hasAuthority('Admin')")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<String> syncCustomer(@PathVariable Integer customerId) {
        log.info("Manual sync customer {} triggered by admin", customerId);
        try {
            vectorSyncService.syncCustomer(customerId);
            return ApiResponse.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Customer sync completed successfully")
                    .result("Customer " + customerId + " synced to Qdrant")
                    .build();
        } catch (Exception e) {
            log.error("Error syncing customer {}", customerId, e);
            return ApiResponse.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to sync customer: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }
    
    /**
     * Sync một range customer IDs vào Qdrant (để sync lại các customer đã miss)
     * Ví dụ: POST /api/vector-sync/customers/range?from=7&to=14
     */
    @PostMapping("/customers/range")
    @PreAuthorize("hasAuthority('Admin')")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<String> syncCustomerRange(
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer to) {
        log.info("Manual sync customer range from {} to {} triggered by admin", from, to);
        try {
            int successCount = 0;
            int failCount = 0;
            
            // Nếu không có from/to, sync tất cả customers
            if (from == null && to == null) {
                vectorSyncService.syncAllCustomers();
                return ApiResponse.<String>builder()
                        .status(HttpStatus.OK.value())
                        .message("All customers sync completed")
                        .result("Sync completed. Check logs for details.")
                        .build();
            }
            
            // Sync range
            int startId = from != null ? from : 1;
            int endId = to != null ? to : Integer.MAX_VALUE;
            
            for (int customerId = startId; customerId <= endId; customerId++) {
                try {
                    vectorSyncService.syncCustomer(customerId);
                    successCount++;
                    log.debug("Synced customer {}", customerId);
                } catch (Exception e) {
                    failCount++;
                    log.warn("Failed to sync customer {}: {}", customerId, e.getMessage());
                }
            }
            
            return ApiResponse.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Customer range sync completed")
                    .result(String.format("Synced %d customers, %d failed (range: %d-%d)", 
                            successCount, failCount, startId, endId))
                    .build();
        } catch (Exception e) {
            log.error("Error syncing customer range", e);
            return ApiResponse.<String>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to sync customer range: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }
    
    /**
     * Kiểm tra trạng thái collections trong Qdrant
     */
    @GetMapping("/status")
    @PreAuthorize("hasAuthority('Admin')")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<CollectionStatus> getStatus() {
        boolean productsEmpty = vectorSyncService.isCollectionEmpty("products");
        boolean customersEmpty = vectorSyncService.isCollectionEmpty("customers");
        
        CollectionStatus status = new CollectionStatus();
        status.productsEmpty = productsEmpty;
        status.customersEmpty = customersEmpty;
        status.message = String.format("Products: %s, Customers: %s", 
                productsEmpty ? "Empty" : "Has data",
                customersEmpty ? "Empty" : "Has data");
        
        return ApiResponse.<CollectionStatus>builder()
                .status(HttpStatus.OK.value())
                .message("Collection status retrieved")
                .result(status)
                .build();
    }
    
    /**
     * Kiểm tra quota embedding của Gemini API
     * Test bằng cách gọi embedding API với text ngắn
     * LƯU Ý: Sẽ tốn 1 embedding request để test
     */
    @GetMapping("/quota-check")
    @PreAuthorize("hasAuthority('Admin')")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<QuotaStatus> checkQuota() {
        log.info("Checking Gemini API embedding quota...");
        
        QuotaStatus quotaStatus = new QuotaStatus();
        quotaStatus.apiKeyConfigured = false;
        quotaStatus.quotaAvailable = false;
        quotaStatus.message = "Unknown";
        quotaStatus.dimension = 0;
        
        try {
            // Test với text ngắn
            String testText = "test quota check";
            var embedding = geminiService.generateEmbedding(testText);
            
            if (embedding.isEmpty()) {
                quotaStatus.apiKeyConfigured = false;
                quotaStatus.quotaAvailable = false;
                quotaStatus.message = "API key not configured or quota exceeded. Check logs for details.";
            } else {
                quotaStatus.apiKeyConfigured = true;
                quotaStatus.quotaAvailable = true;
                quotaStatus.dimension = embedding.size();
                quotaStatus.message = String.format("✅ Quota available! Embedding dimension: %d", embedding.size());
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            
            if (errorMsg.contains("429") || errorMsg.contains("TooManyRequests")) {
                quotaStatus.apiKeyConfigured = true;
                quotaStatus.quotaAvailable = false;
                quotaStatus.message = "❌ Rate limit (429): Quota đã hết hoặc đang bị rate limit. Đợi 24h để reset.";
            } else if (errorMsg.contains("quota") || errorMsg.contains("Quota")) {
                quotaStatus.apiKeyConfigured = true;
                quotaStatus.quotaAvailable = false;
                quotaStatus.message = "❌ Quota exceeded: Đã vượt quá giới hạn free tier (~1500 requests/day). Đợi 24h để reset.";
            } else {
                quotaStatus.apiKeyConfigured = false;
                quotaStatus.quotaAvailable = false;
                quotaStatus.message = "❌ Error: " + errorMsg;
            }
        }
        
        return ApiResponse.<QuotaStatus>builder()
                .status(HttpStatus.OK.value())
                .message("Quota check completed")
                .result(quotaStatus)
                .build();
    }
    
    /**
     * DTO cho collection status
     */
    public static class CollectionStatus {
        public boolean productsEmpty;
        public boolean customersEmpty;
        public String message;
    }
    
    /**
     * DTO cho quota status
     */
    public static class QuotaStatus {
        public boolean apiKeyConfigured;
        public boolean quotaAvailable;
        public String message;
        public int dimension;
    }
}

