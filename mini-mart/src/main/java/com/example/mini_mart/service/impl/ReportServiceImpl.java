package com.example.mini_mart.service.impl;

import com.example.mini_mart.dto.response.*;
import com.example.mini_mart.entity.ExportReceipt;
import com.example.mini_mart.entity.ExportReceiptItem;
import com.example.mini_mart.entity.ImportReceipt;
import com.example.mini_mart.entity.ImportReceiptItem;
import com.example.mini_mart.entity.Product;
import com.example.mini_mart.repository.ExportReceiptItemRepository;
import com.example.mini_mart.repository.ExportReceiptRepository;
import com.example.mini_mart.repository.ImportReceiptItemRepository;
import com.example.mini_mart.repository.ImportReceiptRepository;
import com.example.mini_mart.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ImportReceiptItemRepository importReceiptItemRepository;
    private final ImportReceiptRepository importReceiptRepository;
    private final ExportReceiptRepository exportReceiptRepository;
    private final ExportReceiptItemRepository exportReceiptItemRepository;

    @Override
    public List<StockReportResponse> getCurrentStock() {
        List<ImportReceiptItem> availableItems = importReceiptItemRepository.findAll().stream()
                .filter(item -> item.getRemainingQuantity() > 0)
                .collect(Collectors.toList());

        Map<Product, Integer> stockByProduct = availableItems.stream()
                .collect(Collectors.groupingBy(
                        ImportReceiptItem::getProduct,
                        Collectors.summingInt(ImportReceiptItem::getRemainingQuantity)
                ));

        return stockByProduct.entrySet().stream()
                .map(entry -> StockReportResponse.builder()
                        .productId(entry.getKey().getId())
                        .productName(entry.getKey().getName())
                        .productSku(entry.getKey().getSku())
                        .totalStock(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpiringItemResponse> getExpiringItems(LocalDate beforeDate) {
        if (beforeDate == null) {
            beforeDate = LocalDate.now().plusDays(30);
        }

        List<ImportReceiptItem> expiringItems = importReceiptItemRepository.findExpiringItems(beforeDate);

        return expiringItems.stream()
                .map(item -> {
                    long daysUntilExpiry = item.getExpireDate() != null
                            ? ChronoUnit.DAYS.between(LocalDate.now(), item.getExpireDate())
                            : Long.MAX_VALUE;

                    return ExpiringItemResponse.builder()
                            .importReceiptItemId(item.getId())
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getName())
                            .productSku(item.getProduct().getSku())
                            .remainingQuantity(item.getRemainingQuantity())
                            .expireDate(item.getExpireDate())
                            .daysUntilExpiry(daysUntilExpiry)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public ImportExportSummaryResponse getImportExportSummary(Instant startDate, Instant endDate) {
        List<ImportReceipt> importReceipts = importReceiptRepository.findByDateRange(startDate, endDate);

        List<ExportReceipt> exportReceipts = exportReceiptRepository.findByDateRange(startDate, endDate);

        int totalImportQuantity = 0;
        BigDecimal totalImportValue = BigDecimal.ZERO;
        for (ImportReceipt importReceipt : importReceipts) {
            List<ImportReceiptItem> items = importReceiptItemRepository.findByImportReceiptId(importReceipt.getId());
            for (ImportReceiptItem item : items) {
                totalImportQuantity += item.getQuantity();
                totalImportValue = totalImportValue.add(
                        item.getImportPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                );
            }
        }

        int totalExportQuantity = 0;
        BigDecimal totalExportValue = BigDecimal.ZERO;
        for (ExportReceipt exportReceipt : exportReceipts) {
            List<com.example.mini_mart.entity.ExportReceiptItem> items = exportReceiptItemRepository
                    .findByExportReceiptId(exportReceipt.getId());
            for (com.example.mini_mart.entity.ExportReceiptItem item : items) {
                totalExportQuantity += item.getQuantity();
                totalExportValue = totalExportValue.add(
                        item.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                );
            }
        }

        return ImportExportSummaryResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalImportReceipts(importReceipts.size())
                .totalExportReceipts(exportReceipts.size())
                .totalImportQuantity(totalImportQuantity)
                .totalExportQuantity(totalExportQuantity)
                .totalImportValue(totalImportValue)
                .totalExportValue(totalExportValue)
                .build();
    }
    
    @Override
    public List<TopExportedProductResponse> getTopExportedProducts(Instant startDate, Instant endDate, int limit) {
        List<ExportReceipt> exportReceipts = exportReceiptRepository.findByDateRange(startDate, endDate);
        
        Map<Product, Integer> quantityByProduct = new HashMap<>();
        Map<Product, Long> valueByProduct = new HashMap<>();
        
        for (ExportReceipt exportReceipt : exportReceipts) {
            List<ExportReceiptItem> items = exportReceiptItemRepository.findByExportReceiptId(exportReceipt.getId());
            for (ExportReceiptItem item : items) {
                Product product = item.getProduct();
                quantityByProduct.merge(product, item.getQuantity(), Integer::sum);
                long itemValue = item.getSellingPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))
                        .longValue();
                valueByProduct.merge(product, itemValue, Long::sum);
            }
        }
        
        return quantityByProduct.entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Product product = entry.getKey();
                    return TopExportedProductResponse.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .productSku(product.getSku())
                            .totalQuantity(entry.getValue())
                            .totalValue(valueByProduct.getOrDefault(product, 0L))
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<StockReportResponse> getStockByCategory(Integer categoryId) {
        List<ImportReceiptItem> availableItems = importReceiptItemRepository.findAll().stream()
                .filter(item -> item.getRemainingQuantity() > 0)
                .filter(item -> item.getProduct().getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
        
        Map<Product, Integer> stockByProduct = availableItems.stream()
                .collect(Collectors.groupingBy(
                        ImportReceiptItem::getProduct,
                        Collectors.summingInt(ImportReceiptItem::getRemainingQuantity)
                ));
        
        return stockByProduct.entrySet().stream()
                .map(entry -> StockReportResponse.builder()
                        .productId(entry.getKey().getId())
                        .productName(entry.getKey().getName())
                        .productSku(entry.getKey().getSku())
                        .totalStock(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TopExportingUserResponse> getTopExportingUsers(
            Instant startDate, Instant endDate, int limit) {
        List<ExportReceipt> exportReceipts = exportReceiptRepository.findByDateRange(startDate, endDate);
        
        Map<com.example.mini_mart.entity.User, Integer> quantityByUser = new HashMap<>();
        Map<com.example.mini_mart.entity.User, BigDecimal> amountByUser = new HashMap<>();
        
        for (ExportReceipt exportReceipt : exportReceipts) {
            com.example.mini_mart.entity.User user = exportReceipt.getUser();
            List<ExportReceiptItem> items = exportReceiptItemRepository.findByExportReceiptId(exportReceipt.getId());
            
            int totalQuantity = 0;
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (ExportReceiptItem item : items) {
                totalQuantity += item.getQuantity();
                BigDecimal itemValue = item.getSellingPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(itemValue);
            }
            
            quantityByUser.merge(user, totalQuantity, Integer::sum);
            amountByUser.merge(user, totalAmount, BigDecimal::add);
        }
        
        return quantityByUser.entrySet().stream()
                .sorted(Map.Entry.<com.example.mini_mart.entity.User, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    com.example.mini_mart.entity.User user = entry.getKey();
                    return com.example.mini_mart.dto.response.TopExportingUserResponse.builder()
                            .userId(user.getId())
                            .fullName(user.getFullName())
                            .totalExportQuantity(entry.getValue())
                            .totalExportAmount(amountByUser.getOrDefault(user, BigDecimal.ZERO))
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public RevenueReportResponse getRevenueReport(
            Instant startDate, Instant endDate, String groupBy) {
        List<ExportReceipt> exportReceipts = exportReceiptRepository.findByDateRange(startDate, endDate);
        
        Map<String, RevenueReportResponse.RevenueDataPoint> revenueMap = new HashMap<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        
        for (ExportReceipt exportReceipt : exportReceipts) {
            List<ExportReceiptItem> items = exportReceiptItemRepository.findByExportReceiptId(exportReceipt.getId());
            
            String key;
            LocalDate dateValue;
            if ("MONTH".equalsIgnoreCase(groupBy)) {
                dateValue = exportReceipt.getExportDate().atZone(ZoneId.systemDefault()).toLocalDate();
                key = dateValue.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                dateValue = dateValue.withDayOfMonth(1); // Đầu tháng
            } else {
                dateValue = exportReceipt.getExportDate().atZone(ZoneId.systemDefault()).toLocalDate();
                key = dateValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            
            final String finalKey = key;
            final LocalDate finalDateValue = dateValue;
            
            for (ExportReceiptItem item : items) {
                final ExportReceiptItem finalItem = item;
                final BigDecimal itemRevenue = item.getSellingPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                totalRevenue = totalRevenue.add(itemRevenue);
                
                revenueMap.compute(finalKey, (k, v) -> {
                    if (v == null) {
                        return RevenueReportResponse.RevenueDataPoint.builder()
                                .date(finalKey)
                                .dateValue(finalDateValue)
                                .revenue(itemRevenue)
                                .quantity(finalItem.getQuantity())
                                .build();
                    } else {
                        v.setRevenue(v.getRevenue().add(itemRevenue));
                        v.setQuantity(v.getQuantity() + finalItem.getQuantity());
                        return v;
                    }
                });
            }
        }
        
        List<RevenueReportResponse.RevenueDataPoint> dataPoints =
                revenueMap.values().stream()
                        .sorted(java.util.Comparator.comparing(
                                RevenueReportResponse.RevenueDataPoint::getDateValue))
                        .collect(Collectors.toList());
        
        return RevenueReportResponse.builder()
                .startDate(startDate.atZone(ZoneId.systemDefault()).toLocalDate())
                .endDate(endDate.atZone(ZoneId.systemDefault()).toLocalDate())
                .groupBy(groupBy != null ? groupBy.toUpperCase() : "DAY")
                .dataPoints(dataPoints)
                .totalRevenue(totalRevenue)
                .build();
    }
}

