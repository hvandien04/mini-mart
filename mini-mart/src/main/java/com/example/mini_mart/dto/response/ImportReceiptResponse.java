package com.example.mini_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportReceiptResponse {
    private Integer id;
    private Integer userId;
    private String userName;
    private Integer supplierId;
    private String supplierName;
    private Instant importDate;
    private Instant createdAt;
    private String note;
    private List<ImportReceiptItemResponse> items;
}

