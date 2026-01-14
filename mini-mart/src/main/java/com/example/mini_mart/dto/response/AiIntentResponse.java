package com.example.mini_mart.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Intent response từ AI
 * AI chỉ làm nhiệm vụ hiểu ngôn ngữ tự nhiên và trả về intent dạng JSON
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiIntentResponse {
    /**
     * Action type: EXPORT_PREVIEW, INVENTORY_QUERY, UNKNOWN
     */
    private String action;
    
    /**
     * Cho EXPORT_PREVIEW
     */
    @JsonProperty("product_name")
    private String productName; // Deprecated: Dùng products thay vì product_name
    
    @JsonProperty("quantity")
    private Integer quantity; // Deprecated: Dùng products thay vì quantity
    
    @JsonProperty("customer_name")
    private String customerName;
    
    /**
     * Danh sách sản phẩm (hỗ trợ nhiều sản phẩm)
     * Format: [{"product_name": "coca", "quantity": 5}, {"product_name": "bánh oreo", "quantity": 5}]
     */
    @JsonProperty("products")
    private List<ProductQuantity> products;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductQuantity {
        @JsonProperty("product_name")
        private String productName;
        
        @JsonProperty("quantity")
        private Integer quantity;
    }
    
    /**
     * Cho INVENTORY_QUERY
     */
    @JsonProperty("query_type")
    private String queryType; // STOCK, EXPIRE_SOON, TOP_EXPORT, INVENTORY_SUMMARY
    
    @JsonProperty("time_range")
    private String timeRange; // "today", "7days", "30days", etc.
}

