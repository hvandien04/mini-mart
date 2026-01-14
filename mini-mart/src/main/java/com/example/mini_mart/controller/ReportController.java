package com.example.mini_mart.controller;

import com.example.mini_mart.dto.response.ApiResponse;
import com.example.mini_mart.dto.response.ExpiringItemResponse;
import com.example.mini_mart.dto.response.ImportExportSummaryResponse;
import com.example.mini_mart.dto.response.StockReportResponse;
import com.example.mini_mart.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Report", description = "Report APIs for inventory management")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/stock")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    @Operation(summary = "Get current stock report", description = "Get current stock grouped by product (Admin/Staff only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock report retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<List<StockReportResponse>> getCurrentStock() {
        return ApiResponse.<List<StockReportResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Stock report retrieved successfully")
                .result(reportService.getCurrentStock())
                .build();
    }

    @GetMapping("/expiring-items")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    @Operation(summary = "Get expiring items", description = "Get list of items that are expiring before a specific date (default: 30 days from now) (Admin/Staff only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Expiring items report retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<List<ExpiringItemResponse>> getExpiringItems(
            @Parameter(description = "Date to check expiration (ISO format: yyyy-MM-dd). Default: 30 days from now")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beforeDate) {
        return ApiResponse.<List<ExpiringItemResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Expiring items report retrieved successfully")
                .result(reportService.getExpiringItems(beforeDate))
                .build();
    }

    @GetMapping("/import-export-summary")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    @Operation(summary = "Get import-export summary", description = "Get summary of import and export receipts within a date range (Admin/Staff only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Import-Export summary retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<ImportExportSummaryResponse> getImportExportSummary(
            @Parameter(description = "Start date (ISO format: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date (ISO format: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        return ApiResponse.<ImportExportSummaryResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Import-Export summary retrieved successfully")
                .result(reportService.getImportExportSummary(startDate, endDate))
                .build();
    }
    
    @GetMapping("/top-exporting-users")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    @Operation(summary = "Get top exporting users", description = "Get top users who exported the most (by quantity or value) within a date range (Admin/Staff only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Top exporting users retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<List<com.example.mini_mart.dto.response.TopExportingUserResponse>> getTopExportingUsers(
            @Parameter(description = "Start date (ISO format: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date (ISO format: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @Parameter(description = "Number of top users to return (default: 10)") 
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.<List<com.example.mini_mart.dto.response.TopExportingUserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Top exporting users retrieved successfully")
                .result(reportService.getTopExportingUsers(startDate, endDate, limit))
                .build();
    }
    
    @GetMapping("/revenue")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    @Operation(summary = "Get revenue report", description = "Get revenue report grouped by day or month. Revenue = sum(quantity * sellingPrice) from ExportReceiptItem (Admin/Staff only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Revenue report retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ApiResponse<com.example.mini_mart.dto.response.RevenueReportResponse> getRevenueReport(
            @Parameter(description = "Start date (ISO format: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date (ISO format: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @Parameter(description = "Group by: DAY or MONTH (default: DAY)") 
            @RequestParam(defaultValue = "DAY") String groupBy) {
        return ApiResponse.<com.example.mini_mart.dto.response.RevenueReportResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Revenue report retrieved successfully")
                .result(reportService.getRevenueReport(startDate, endDate, groupBy))
                .build();
    }
}
