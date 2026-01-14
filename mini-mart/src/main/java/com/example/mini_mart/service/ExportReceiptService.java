package com.example.mini_mart.service;

import com.example.mini_mart.dto.request.ExportReceiptCreateRequest;
import com.example.mini_mart.dto.response.ExportReceiptResponse;

import java.util.List;

public interface ExportReceiptService {
    ExportReceiptResponse create(ExportReceiptCreateRequest request, String username);
    ExportReceiptResponse getById(Integer id);
    List<ExportReceiptResponse> getAll();
}

