package com.example.mini_mart.controller;

import com.example.mini_mart.dto.request.UserCreateRequest;
import com.example.mini_mart.dto.request.UserUpdateRequest;
import com.example.mini_mart.dto.response.ApiResponse;
import com.example.mini_mart.dto.response.UserResponse;
import com.example.mini_mart.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('Admin')")
    @Operation(summary = "Create user", description = "Create a new user (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("User created successfully")
                .result(userService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    @Operation(summary = "Update user", description = "Update an existing user (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<UserResponse> update(
            @Parameter(description = "User ID") @PathVariable Integer id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User updated successfully")
                .result(userService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    @Operation(summary = "Delete user", description = "Delete a user by ID (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<Void> delete(@Parameter(description = "User ID") @PathVariable Integer id) {
        userService.delete(id);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("User deleted successfully")
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by its ID (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<UserResponse> getById(@Parameter(description = "User ID") @PathVariable Integer id) {
        return ApiResponse.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User retrieved successfully")
                .result(userService.getById(id))
                .build();
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAuthority('Admin')")
    @Operation(summary = "Get user by username", description = "Retrieve a user by username (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<UserResponse> getByUsername(@Parameter(description = "Username") @PathVariable String username) {
        return ApiResponse.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User retrieved successfully")
                .result(userService.getByUsername(username))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('Admin')")
    @Operation(summary = "Get all users", description = "Retrieve all users (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<List<UserResponse>> getAll() {
        return ApiResponse.<List<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Users retrieved successfully")
                .result(userService.getAll())
                .build();
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('Admin')")
    @Operation(summary = "Get active users", description = "Retrieve all active users (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active users retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<List<UserResponse>> getActiveUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Active users retrieved successfully")
                .result(userService.getActiveUsers())
                .build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('Admin')")
    @Operation(summary = "Update user status", description = "Lock or unlock a user by ID (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<UserResponse> updateStatus(
            @Parameter(description = "User ID") @PathVariable Integer id,
            @Parameter(description = "Active status") @RequestParam Boolean isActive) {
        return ApiResponse.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User status updated successfully")
                .result(userService.updateStatus(id, isActive))
                .build();
    }
}


