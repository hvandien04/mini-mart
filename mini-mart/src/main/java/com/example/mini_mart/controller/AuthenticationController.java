package com.example.mini_mart.controller;

import com.example.mini_mart.dto.request.AuthenticationRequest;
import com.example.mini_mart.dto.request.IntrospectRequest;
import com.example.mini_mart.dto.request.RefreshTokenRequest;
import com.example.mini_mart.dto.response.ApiResponse;
import com.example.mini_mart.dto.response.AuthenticationResponse;
import com.example.mini_mart.dto.response.CurrentUserResponse;
import com.example.mini_mart.dto.response.IntrospectResponse;
import com.example.mini_mart.service.AuthenticateService;
import org.springframework.security.core.Authentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthenticationController {

    private final AuthenticateService authenticateService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user with username and password")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid credentials"
            )
    })
    public ApiResponse<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .result(authenticateService.authenticate(request))
                .build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Refresh access token using refresh token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid refresh token"
            )
    })
    public ApiResponse<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            return ApiResponse.<AuthenticationResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Token refreshed successfully")
                    .result(authenticateService.refreshToken(request.getRefreshToken()))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh token", e);
        }
    }

    @PostMapping("/introspect")
    @Operation(summary = "Introspect Token", description = "Validate and get token information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token introspected successfully",
                    content = @Content(schema = @Schema(implementation = IntrospectResponse.class))
            )
    })
    public ApiResponse<IntrospectResponse> introspect(@Valid @RequestBody IntrospectRequest request) {
        return ApiResponse.<IntrospectResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Token introspected successfully")
                .result(authenticateService.introspect(request))
                .build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get information of currently authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Current user retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CurrentUserResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated"
            )
    })
    public ApiResponse<CurrentUserResponse> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return ApiResponse.<CurrentUserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Current user retrieved successfully")
                .result(authenticateService.getCurrentUser(username))
                .build();
    }
}

