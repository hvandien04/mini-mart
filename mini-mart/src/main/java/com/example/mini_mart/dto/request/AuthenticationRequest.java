package com.example.mini_mart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AuthenticationRequest {

    @Schema(description = "Username of user", example = "admin")
    @NotNull(message = "Username cannot be null")
    private String username;

    @Schema(description = "Password of user", example = "AbcD1234>")
    @NotNull(message = "Password cannot be null")
    private String password;
}
