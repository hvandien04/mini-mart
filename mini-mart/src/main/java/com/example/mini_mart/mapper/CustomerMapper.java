package com.example.mini_mart.mapper;

import com.example.mini_mart.dto.request.CustomerCreateRequest;
import com.example.mini_mart.dto.request.CustomerUpdateRequest;
import com.example.mini_mart.dto.response.CustomerResponse;
import com.example.mini_mart.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exportReceipts", ignore = true)
    Customer toEntity(CustomerCreateRequest request);
    
    CustomerResponse toResponse(Customer customer);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exportReceipts", ignore = true)
    void updateEntity(@MappingTarget Customer customer, CustomerUpdateRequest request);
}

