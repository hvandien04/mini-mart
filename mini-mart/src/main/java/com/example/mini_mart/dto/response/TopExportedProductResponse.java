package com.example.mini_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho top sản phẩm xuất nhiều nhất
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopExportedProductResponse {
    private Integer productId;
    private String productName;
    private String productSku;
    private Integer totalQuantity;
    private Long totalValue;
}


