package com.example.mini_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO cho báo cáo doanh thu
 * Doanh thu = tổng (quantity * sellingPrice) từ ExportReceiptItem
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private String groupBy; // "DAY" hoặc "MONTH"
    private List<RevenueDataPoint> dataPoints;
    private BigDecimal totalRevenue;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueDataPoint {
        private String date; // Format: "yyyy-MM-dd" hoặc "yyyy-MM"
        private LocalDate dateValue; // Để sort
        private BigDecimal revenue;
        private Integer quantity;
    }
}





