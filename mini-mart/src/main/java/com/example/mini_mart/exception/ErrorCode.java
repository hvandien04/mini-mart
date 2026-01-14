package com.example.mini_mart.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(1000, "You do not have permission", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    INCORRECT_PHONE_OR_PASSWORD(1003, "Incorrect username or password", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(1016, "Tên đăng nhập hoặc mật khẩu không đúng", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOCKED(1015, "Tài khoản đã bị khóa, vui lòng liên hệ quản lý", HttpStatus.FORBIDDEN),
    WRONG_PASSWORD(1004, "password is incorrect", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCHED(1005, "Password not matched", HttpStatus.BAD_REQUEST),
    SAME_PASSWORD(1006, "New password must be different from old password", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_ALREADY_EXISTS(1007, "Phone number already exists", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS(1012, "Username already exists", HttpStatus.CONFLICT),
    PHONE_NUMBER_NOT_DRIVER(1008, "Phone number not linked to any driver", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_NOT_ALLOWED(1009,"access token cannot be used to refresh", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_NOT_FOUND(1010, "Phone number not found", HttpStatus.NOT_FOUND),
    LOGIN_BLOCKED(1011, "Your account has been temporarily locked due to multiple failed login attempts. Please try again later.", HttpStatus.TOO_MANY_REQUESTS),
    
    // Category errors
    CATEGORY_NOT_FOUND(2001, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_EXISTS(2002, "Category name already exists", HttpStatus.CONFLICT),
    
    // Product errors
    PRODUCT_NOT_FOUND(3001, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_SKU_EXISTS(3002, "Product SKU already exists", HttpStatus.CONFLICT),
    
    // Supplier errors
    SUPPLIER_NOT_FOUND(4001, "Supplier not found", HttpStatus.NOT_FOUND),
    
    // Customer errors
    CUSTOMER_NOT_FOUND(5001, "Customer not found", HttpStatus.NOT_FOUND),
    
    // Import errors
    IMPORT_RECEIPT_NOT_FOUND(6001, "Import receipt not found", HttpStatus.NOT_FOUND),
    INVALID_IMPORT_QUANTITY(6002, "Import quantity must be greater than 0", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_BELONG_TO_SUPPLIER(6003, "Sản phẩm không thuộc nhà cung cấp đã chọn", HttpStatus.BAD_REQUEST),
    
    // Export errors
    EXPORT_RECEIPT_NOT_FOUND(7001, "Export receipt not found", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK(7002, "Insufficient stock", HttpStatus.BAD_REQUEST),
    INVALID_EXPORT_QUANTITY(7003, "Export quantity must be greater than 0", HttpStatus.BAD_REQUEST);

    private final Integer code;
    private final String message;
    private final HttpStatusCode status;

    ErrorCode(Integer code, String message,HttpStatusCode status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}