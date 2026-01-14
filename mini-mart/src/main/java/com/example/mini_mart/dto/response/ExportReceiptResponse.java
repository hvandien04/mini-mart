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
public class ExportReceiptResponse {
    private Integer id;
    private Integer userId;
    private String userName;
    private Integer customerId;
    private String customerName;
    private Instant exportDate;
    private Instant createdAt;
    private String note;
    private List<ExportReceiptItemResponse> items;
}

