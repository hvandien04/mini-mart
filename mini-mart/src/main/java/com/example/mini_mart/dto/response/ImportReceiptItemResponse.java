package com.example.mini_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportReceiptItemResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productSku;
    private Integer quantity;
    private Integer remainingQuantity;
    private BigDecimal importPrice;
    private LocalDate expireDate;
    private String note;
}

