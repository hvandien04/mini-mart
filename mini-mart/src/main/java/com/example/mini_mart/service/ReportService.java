package com.example.mini_mart.service;

import com.example.mini_mart.dto.response.ExpiringItemResponse;
import com.example.mini_mart.dto.response.ImportExportSummaryResponse;
import com.example.mini_mart.dto.response.StockReportResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<StockReportResponse> getCurrentStock();
    List<ExpiringItemResponse> getExpiringItems(LocalDate beforeDate);
    ImportExportSummaryResponse getImportExportSummary(Instant startDate, Instant endDate);
    
    /**
     * Lấy top sản phẩm xuất nhiều nhất trong khoảng thời gian
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param limit Số lượng top sản phẩm
     * @return Danh sách top sản phẩm
     */
    List<com.example.mini_mart.dto.response.TopExportedProductResponse> getTopExportedProducts(Instant startDate, Instant endDate, int limit);
    
    /**
     * Lấy tồn kho theo category
     * @param categoryId Category ID
     * @return Danh sách tồn kho theo category
     */
    List<StockReportResponse> getStockByCategory(Integer categoryId);
    
    /**
     * Lấy top nhân viên xuất kho nhiều nhất
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param limit Số lượng top nhân viên
     * @return Danh sách top nhân viên sắp xếp theo số lượng xuất giảm dần
     */
    List<com.example.mini_mart.dto.response.TopExportingUserResponse> getTopExportingUsers(
        Instant startDate, 
        Instant endDate, 
        int limit
    );
    
    /**
     * Lấy báo cáo doanh thu theo ngày/tháng
     * Doanh thu = tổng (quantity * sellingPrice) từ ExportReceiptItem
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param groupBy "DAY" hoặc "MONTH"
     * @return Báo cáo doanh thu
     */
    com.example.mini_mart.dto.response.RevenueReportResponse getRevenueReport(
        Instant startDate,
        Instant endDate,
        String groupBy
    );
}

