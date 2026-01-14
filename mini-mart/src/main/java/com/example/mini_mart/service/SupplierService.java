package com.example.mini_mart.service;

import com.example.mini_mart.dto.request.SupplierCreateRequest;
import com.example.mini_mart.dto.request.SupplierUpdateRequest;
import com.example.mini_mart.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {
    SupplierResponse create(SupplierCreateRequest request);
    SupplierResponse update(Integer id, SupplierUpdateRequest request);
    void delete(Integer id);
    SupplierResponse getById(Integer id);
    List<SupplierResponse> getAll();
}

