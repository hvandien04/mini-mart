package com.example.mini_mart.service;

import com.example.mini_mart.dto.request.CategoryCreateRequest;
import com.example.mini_mart.dto.request.CategoryUpdateRequest;
import com.example.mini_mart.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryCreateRequest request);
    CategoryResponse update(Integer id, CategoryUpdateRequest request);
    void delete(Integer id);
    CategoryResponse getById(Integer id);
    List<CategoryResponse> getAll();
    List<CategoryResponse> getActiveCategories();
}

