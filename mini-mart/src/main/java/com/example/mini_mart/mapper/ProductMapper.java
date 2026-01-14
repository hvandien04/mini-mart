package com.example.mini_mart.mapper;

import com.example.mini_mart.dto.request.ProductCreateRequest;
import com.example.mini_mart.dto.request.ProductUpdateRequest;
import com.example.mini_mart.dto.response.ProductResponse;
import com.example.mini_mart.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "exportReceiptItems", ignore = true)
    @Mapping(target = "importReceiptItems", ignore = true)
    Product toEntity(ProductCreateRequest request);
    
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    ProductResponse toResponse(Product product);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "exportReceiptItems", ignore = true)
    @Mapping(target = "importReceiptItems", ignore = true)
    void updateEntity(@MappingTarget Product product, ProductUpdateRequest request);
}

