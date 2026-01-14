package com.example.mini_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private Integer supplierId;
    private String supplierName;
    private String sku;
    private String name;
    private String brand;
    private String description;
    private String imageUrl;
    private String unit;
    private BigDecimal sellingPrice;
    private Boolean isActive;
    private Instant createdAt;
}

