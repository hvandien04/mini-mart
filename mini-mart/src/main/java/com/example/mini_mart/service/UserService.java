package com.example.mini_mart.service;

import com.example.mini_mart.dto.request.UserCreateRequest;
import com.example.mini_mart.dto.request.UserUpdateRequest;
import com.example.mini_mart.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(UserCreateRequest request);
    UserResponse update(Integer id, UserUpdateRequest request);
    void delete(Integer id);
    UserResponse getById(Integer id);
    UserResponse getByUsername(String username);
    List<UserResponse> getAll();
    List<UserResponse> getActiveUsers();
    UserResponse updateStatus(Integer id, Boolean isActive);
}


