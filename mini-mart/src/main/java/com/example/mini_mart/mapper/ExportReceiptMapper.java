package com.example.mini_mart.mapper;

import com.example.mini_mart.dto.response.ExportReceiptItemResponse;
import com.example.mini_mart.dto.response.ExportReceiptResponse;
import com.example.mini_mart.entity.ExportReceipt;
import com.example.mini_mart.entity.ExportReceiptItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExportReceiptMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userName")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.fullName", target = "customerName")
    @Mapping(target = "items", ignore = true)
    ExportReceiptResponse toResponse(ExportReceipt exportReceipt);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.sku", target = "productSku")
    @Mapping(source = "importReceiptItem.id", target = "importReceiptItemId")
    ExportReceiptItemResponse toItemResponse(ExportReceiptItem item);

    List<ExportReceiptItemResponse> toItemResponseList(List<ExportReceiptItem> items);
}

