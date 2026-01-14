package com.example.mini_mart.mapper;

import com.example.mini_mart.dto.request.ImportReceiptItemRequest;
import com.example.mini_mart.dto.response.ImportReceiptItemResponse;
import com.example.mini_mart.dto.response.ImportReceiptResponse;
import com.example.mini_mart.entity.ImportReceipt;
import com.example.mini_mart.entity.ImportReceiptItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ImportReceiptMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userName")
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    @Mapping(target = "items", ignore = true)
    ImportReceiptResponse toResponse(ImportReceipt importReceipt);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.sku", target = "productSku")
    ImportReceiptItemResponse toItemResponse(ImportReceiptItem item);

    List<ImportReceiptItemResponse> toItemResponseList(List<ImportReceiptItem> items);
}

