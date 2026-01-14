package com.example.mini_mart.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResetPassword {
    private String phone;
    private String otp;
    private String password;
    private String confirmPassword;
}
