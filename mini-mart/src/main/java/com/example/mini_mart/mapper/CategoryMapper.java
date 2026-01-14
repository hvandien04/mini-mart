package com.example.mini_mart.mapper;

import com.example.mini_mart.dto.request.CategoryCreateRequest;
import com.example.mini_mart.dto.request.CategoryUpdateRequest;
import com.example.mini_mart.dto.response.CategoryResponse;
import com.example.mini_mart.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryCreateRequest request);
    
    CategoryResponse toResponse(Category category);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntity(@MappingTarget Category category, CategoryUpdateRequest request);
}

