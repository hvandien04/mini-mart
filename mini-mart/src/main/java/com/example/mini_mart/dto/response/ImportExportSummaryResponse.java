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
public class ImportExportSummaryResponse {
    private Instant startDate;
    private Instant endDate;
    private Integer totalImportReceipts;
    private Integer totalExportReceipts;
    private Integer totalImportQuantity;
    private Integer totalExportQuantity;
    private BigDecimal totalImportValue;
    private BigDecimal totalExportValue;
}

