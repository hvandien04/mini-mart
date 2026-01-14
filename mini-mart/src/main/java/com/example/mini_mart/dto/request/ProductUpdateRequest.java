package com.example.mini_mart.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    private Integer categoryId;
    private Integer supplierId;

    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;

    @Size(max = 500, message = "Product name must not exceed 500 characters")
    private String name;

    @Size(max = 250, message = "Brand must not exceed 250 characters")
    private String brand;

    private String description;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @Size(max = 50, message = "Unit must not exceed 50 characters")
    private String unit;

    @Positive(message = "Selling price must be positive")
    private BigDecimal sellingPrice;

    private Boolean isActive;
}

