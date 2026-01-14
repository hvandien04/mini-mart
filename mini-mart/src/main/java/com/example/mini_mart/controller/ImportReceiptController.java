package com.example.mini_mart.controller;

import com.example.mini_mart.dto.request.ImportReceiptCreateRequest;
import com.example.mini_mart.dto.response.ApiResponse;
import com.example.mini_mart.dto.response.ImportReceiptResponse;
import com.example.mini_mart.service.ImportReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/import-receipts")
@RequiredArgsConstructor
@Tag(name = "Import Receipt", description = "Import receipt (Nhập kho) management APIs")
public class ImportReceiptController {

    private final ImportReceiptService importReceiptService;

    @PostMapping
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    @Operation(summary = "Create import receipt", description = "Create a new import receipt with batch items (Admin/Staff only). Each item creates a batch with quantity and expire date.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Import receipt created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<ImportReceiptResponse> create(
            @Valid @RequestBody ImportReceiptCreateRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ApiResponse.<ImportReceiptResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Import receipt created successfully")
                .result(importReceiptService.create(request, username))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get import receipt by ID", description = "Retrieve an import receipt by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Import receipt retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Import receipt not found")
    })
    public ApiResponse<ImportReceiptResponse> getById(@Parameter(description = "Import Receipt ID") @PathVariable Integer id) {
        return ApiResponse.<ImportReceiptResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Import receipt retrieved successfully")
                .result(importReceiptService.getById(id))
                .build();
    }

    @GetMapping
    @Operation(summary = "Get all import receipts", description = "Retrieve all import receipts")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Import receipts retrieved successfully")
    })
    public ApiResponse<List<ImportReceiptResponse>> getAll() {
        return ApiResponse.<List<ImportReceiptResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Import receipts retrieved successfully")
                .result(importReceiptService.getAll())
                .build();
    }
    
    @GetMapping("/{id}/items")
    @Operation(summary = "Get import receipt items with filter", description = "Retrieve import receipt items filtered by remaining status")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Import receipt items retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Import receipt not found")
    })
    public ApiResponse<ImportReceiptResponse> getItemsWithFilter(
            @Parameter(description = "Import Receipt ID") @PathVariable Integer id,
            @Parameter(description = "Filter by remaining status: true = còn hàng (remainingQuantity > 0), false = hết hàng (remainingQuantity = 0), null = tất cả") 
            @RequestParam(required = false) Boolean hasRemaining) {
        return ApiResponse.<ImportReceiptResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Import receipt items retrieved successfully")
                .result(importReceiptService.getByIdWithFilter(id, hasRemaining))
                .build();
    }
}
