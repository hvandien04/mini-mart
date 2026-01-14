package com.example.mini_mart.controller;

import com.example.mini_mart.dto.request.CustomerCreateRequest;
import com.example.mini_mart.dto.request.CustomerUpdateRequest;
import com.example.mini_mart.dto.response.ApiResponse;
import com.example.mini_mart.dto.response.CustomerResponse;
import com.example.mini_mart.service.CustomerService;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management APIs")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    @Operation(summary = "Create customer", description = "Create a new customer (Admin/Staff only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<CustomerResponse> create(@Valid @RequestBody CustomerCreateRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Customer created successfully")
                .result(customerService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    @Operation(summary = "Update customer", description = "Update an existing customer (Admin/Staff only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<CustomerResponse> update(
            @Parameter(description = "Customer ID") @PathVariable Integer id,
            @Valid @RequestBody CustomerUpdateRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Customer updated successfully")
                .result(customerService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    @Operation(summary = "Delete customer", description = "Delete a customer by ID (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<Void> delete(@Parameter(description = "Customer ID") @PathVariable Integer id) {
        customerService.delete(id);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Customer deleted successfully")
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve a customer by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ApiResponse<CustomerResponse> getById(@Parameter(description = "Customer ID") @PathVariable Integer id) {
        return ApiResponse.<CustomerResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Customer retrieved successfully")
                .result(customerService.getById(id))
                .build();
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve all customers")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    })
    public ApiResponse<List<CustomerResponse>> getAll() {
        return ApiResponse.<List<CustomerResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Customers retrieved successfully")
                .result(customerService.getAll())
                .build();
    }
}
