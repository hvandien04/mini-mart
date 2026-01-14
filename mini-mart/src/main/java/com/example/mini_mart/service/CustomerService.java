package com.example.mini_mart.service;

import com.example.mini_mart.dto.request.CustomerCreateRequest;
import com.example.mini_mart.dto.request.CustomerUpdateRequest;
import com.example.mini_mart.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponse create(CustomerCreateRequest request);
    CustomerResponse update(Integer id, CustomerUpdateRequest request);
    void delete(Integer id);
    CustomerResponse getById(Integer id);
    List<CustomerResponse> getAll();
}

