package com.example.mini_mart.service.impl;

import com.example.mini_mart.dto.request.CustomerCreateRequest;
import com.example.mini_mart.dto.request.CustomerUpdateRequest;
import com.example.mini_mart.dto.response.CustomerResponse;
import com.example.mini_mart.entity.Customer;
import com.example.mini_mart.exception.AppException;
import com.example.mini_mart.exception.ErrorCode;
import com.example.mini_mart.mapper.CustomerMapper;
import com.example.mini_mart.repository.CustomerRepository;
import com.example.mini_mart.service.CustomerService;
import com.example.mini_mart.service.VectorSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final VectorSyncService vectorSyncService;

    @Override
    @Transactional
    public CustomerResponse create(CustomerCreateRequest request) {
        Customer customer = customerMapper.toEntity(request);
        customer = customerRepository.save(customer);
        
        try {
            vectorSyncService.syncCustomer(customer.getId());
            log.debug("Synced new customer {} to Qdrant", customer.getId());
        } catch (Exception e) {
            log.warn("Failed to sync customer {} to Qdrant: {}. Customer was created successfully.", 
                    customer.getId(), e.getMessage());
        }
        
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse update(Integer id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        customerMapper.updateEntity(customer, request);
        customer = customerRepository.save(customer);
        
        try {
            vectorSyncService.syncCustomer(customer.getId());
            log.debug("Synced updated customer {} to Qdrant", customer.getId());
        } catch (Exception e) {
            log.warn("Failed to sync customer {} to Qdrant: {}. Customer was updated successfully.", 
                    customer.getId(), e.getMessage());
        }
        
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        customerRepository.delete(customer);
        
        try {
            vectorSyncService.deleteCustomer(id);
            log.debug("Deleted customer {} from Qdrant", id);
        } catch (Exception e) {
            log.warn("Failed to delete customer {} from Qdrant: {}", id, e.getMessage());
        }
    }

    @Override
    public CustomerResponse getById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        return customerMapper.toResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }
}

