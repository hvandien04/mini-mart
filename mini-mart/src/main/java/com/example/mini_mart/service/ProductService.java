package com.example.mini_mart.service;

import com.example.mini_mart.dto.request.ProductCreateRequest;
import com.example.mini_mart.dto.request.ProductUpdateRequest;
import com.example.mini_mart.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductCreateRequest request);
    ProductResponse update(Integer id, ProductUpdateRequest request);
    void delete(Integer id);
    ProductResponse getById(Integer id);
    List<ProductResponse> getAll();
    List<ProductResponse> getActiveProducts();
    List<ProductResponse> getByCategoryId(Integer categoryId);
    List<ProductResponse> getBySupplierId(Integer supplierId);
}

