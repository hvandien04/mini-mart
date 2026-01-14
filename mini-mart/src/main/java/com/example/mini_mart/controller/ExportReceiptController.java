package com.example.mini_mart.controller;

import com.example.mini_mart.dto.request.ExportReceiptCreateRequest;
import com.example.mini_mart.dto.response.ApiResponse;
import com.example.mini_mart.dto.response.ExportReceiptResponse;
import com.example.mini_mart.service.ExportReceiptService;
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
@RequestMapping("/api/export-receipts")
@RequiredArgsConstructor
@Tag(name = "Export Receipt", description = "Export receipt (Xuất kho) management APIs with FIFO + expiration date logic")
public class ExportReceiptController {

    private final ExportReceiptService exportReceiptService;

    @PostMapping
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    @Operation(summary = "Create export receipt", description = "Create a new export receipt with FIFO + expiration date logic (Admin/Staff only). " +
            "System automatically selects batches with nearest expiration date first. If one batch is not enough, it will split to next batch.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Export receipt created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or insufficient stock"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<ExportReceiptResponse> create(
            @Valid @RequestBody ExportReceiptCreateRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ApiResponse.<ExportReceiptResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Export receipt created successfully")
                .result(exportReceiptService.create(request, username))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get export receipt by ID", description = "Retrieve an export receipt by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Export receipt retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Export receipt not found")
    })
    public ApiResponse<ExportReceiptResponse> getById(@Parameter(description = "Export Receipt ID") @PathVariable Integer id) {
        return ApiResponse.<ExportReceiptResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Export receipt retrieved successfully")
                .result(exportReceiptService.getById(id))
                .build();
    }

    @GetMapping
    @Operation(summary = "Get all export receipts", description = "Retrieve all export receipts")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Export receipts retrieved successfully")
    })
    public ApiResponse<List<ExportReceiptResponse>> getAll() {
        return ApiResponse.<List<ExportReceiptResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Export receipts retrieved successfully")
                .result(exportReceiptService.getAll())
                .build();
    }
}
