package com.example.mini_mart.mapper;

import com.example.mini_mart.dto.request.SupplierCreateRequest;
import com.example.mini_mart.dto.request.SupplierUpdateRequest;
import com.example.mini_mart.dto.response.SupplierResponse;
import com.example.mini_mart.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SupplierMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "importReceipts", ignore = true)
    Supplier toEntity(SupplierCreateRequest request);
    
    SupplierResponse toResponse(Supplier supplier);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "importReceipts", ignore = true)
    void updateEntity(@MappingTarget Supplier supplier, SupplierUpdateRequest request);
}

