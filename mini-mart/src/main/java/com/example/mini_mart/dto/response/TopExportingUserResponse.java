package com.example.mini_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho top nhân viên xuất kho nhiều nhất
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopExportingUserResponse {
    private Integer userId;
    private String fullName;
    private Integer totalExportQuantity;
    private BigDecimal totalExportAmount;
}





