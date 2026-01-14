package com.example.mini_mart.mapper;

import com.example.mini_mart.dto.request.UserCreateRequest;
import com.example.mini_mart.dto.request.UserUpdateRequest;
import com.example.mini_mart.dto.response.UserResponse;
import com.example.mini_mart.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "exportReceipts", ignore = true)
    @Mapping(target = "importReceipts", ignore = true)
    User toEntity(UserCreateRequest request);
    
    UserResponse toResponse(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "exportReceipts", ignore = true)
    @Mapping(target = "importReceipts", ignore = true)
    void updateEntity(@MappingTarget User user, UserUpdateRequest request);
}


