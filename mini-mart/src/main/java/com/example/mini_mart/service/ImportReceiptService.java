package com.example.mini_mart.service;

import com.example.mini_mart.dto.request.ImportReceiptCreateRequest;
import com.example.mini_mart.dto.response.ImportReceiptResponse;

import java.util.List;

public interface ImportReceiptService {
    ImportReceiptResponse create(ImportReceiptCreateRequest request, String username);
    ImportReceiptResponse getById(Integer id);
    List<ImportReceiptResponse> getAll();
    
    /**
     * Lấy items của import receipt với filter theo trạng thái
     * @param importReceiptId ID của import receipt
     * @param hasRemaining null = tất cả, true = còn hàng (remainingQuantity > 0), false = hết hàng (remainingQuantity = 0)
     * @return ImportReceiptResponse với items đã filter
     */
    ImportReceiptResponse getByIdWithFilter(Integer importReceiptId, Boolean hasRemaining);
}

