package com.example.mini_mart.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class ImportReceiptCreateRequest {
    @NotNull(message = "Supplier ID is required")
    private Integer supplierId;

    @NotNull(message = "Import date is required")
    private Instant importDate;

    @NotEmpty(message = "Import items are required")
    @Valid
    private List<ImportReceiptItemRequest> items;

    private String note;
}

