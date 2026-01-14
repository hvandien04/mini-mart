package com.example.mini_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportReceiptItemResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productSku;
    private Integer importReceiptItemId;
    private Integer quantity;
    private BigDecimal sellingPrice;
    private String note;
}

