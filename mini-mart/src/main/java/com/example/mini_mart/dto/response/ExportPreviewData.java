package com.example.mini_mart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO cho Export Preview data
 * Chứa thông tin preview xuất kho trước khi tạo phiếu xuất thật
 * Hỗ trợ nhiều sản phẩm trong một lệnh xuất
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportPreviewData {
    private CustomerInfo customer;
    
    // Deprecated: Dùng items thay vì product/quantity/unitPrice/totalPrice/stockStatus
    @Deprecated
    private ProductInfo product;
    @Deprecated
    private Integer quantity;
    @Deprecated
    private BigDecimal unitPrice;
    @Deprecated
    private BigDecimal totalPrice;
    @Deprecated
    private String stockStatus; // ENOUGH, INSUFFICIENT
    
    // Danh sách sản phẩm cần xuất
    @Builder.Default
    private List<ExportPreviewItem> items = new ArrayList<>();
    
    // Tổng tiền của tất cả sản phẩm
    private BigDecimal totalAmount;
    
    // Trạng thái tồn kho tổng thể (ENOUGH nếu tất cả đủ, INSUFFICIENT nếu có ít nhất 1 sản phẩm không đủ)
    private String overallStockStatus; // ENOUGH, INSUFFICIENT
    
    private String note;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private Integer id;
        private String name;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        private Integer id;
        private String name;
        private String sku;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExportPreviewItem {
        private ProductInfo product;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private String stockStatus; // ENOUGH, INSUFFICIENT
        private String note;
    }
}


