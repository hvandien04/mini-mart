package com.example.mini_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpiringItemResponse {
    private Integer importReceiptItemId;
    private Integer productId;
    private String productName;
    private String productSku;
    private Integer remainingQuantity;
    private LocalDate expireDate;
    private Long daysUntilExpiry;
}

