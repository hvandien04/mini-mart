package com.example.mini_mart.service.impl;

import com.example.mini_mart.dto.request.SupplierCreateRequest;
import com.example.mini_mart.dto.request.SupplierUpdateRequest;
import com.example.mini_mart.dto.response.SupplierResponse;
import com.example.mini_mart.entity.Supplier;
import com.example.mini_mart.exception.AppException;
import com.example.mini_mart.exception.ErrorCode;
import com.example.mini_mart.mapper.SupplierMapper;
import com.example.mini_mart.repository.SupplierRepository;
import com.example.mini_mart.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    public SupplierResponse create(SupplierCreateRequest request) {
        Supplier supplier = supplierMapper.toEntity(request);
        supplier = supplierRepository.save(supplier);
        return supplierMapper.toResponse(supplier);
    }

    @Override
    @Transactional
    public SupplierResponse update(Integer id, SupplierUpdateRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
        supplierMapper.updateEntity(supplier, request);
        supplier = supplierRepository.save(supplier);
        return supplierMapper.toResponse(supplier);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
        supplierRepository.delete(supplier);
    }

    @Override
    public SupplierResponse getById(Integer id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
        return supplierMapper.toResponse(supplier);
    }

    @Override
    public List<SupplierResponse> getAll() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponse)
                .collect(Collectors.toList());
    }
}

