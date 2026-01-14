package com.example.mini_mart.service.impl;

import com.example.mini_mart.dto.request.ImportReceiptCreateRequest;
import com.example.mini_mart.dto.request.ImportReceiptItemRequest;
import com.example.mini_mart.dto.response.ImportReceiptResponse;
import com.example.mini_mart.entity.*;
import com.example.mini_mart.exception.AppException;
import com.example.mini_mart.exception.ErrorCode;
import com.example.mini_mart.mapper.ImportReceiptMapper;
import com.example.mini_mart.repository.*;
import com.example.mini_mart.service.ImportReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportReceiptServiceImpl implements ImportReceiptService {

    private final ImportReceiptRepository importReceiptRepository;
    private final ImportReceiptItemRepository importReceiptItemRepository;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final ImportReceiptMapper importReceiptMapper;

    @Override
    @Transactional
    public ImportReceiptResponse create(ImportReceiptCreateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));


        for (ImportReceiptItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            
            if (!product.getSupplier().getId().equals(supplier.getId())) {
                throw new AppException(ErrorCode.PRODUCT_NOT_BELONG_TO_SUPPLIER);
            }
        }

        ImportReceipt importReceipt = new ImportReceipt();
        importReceipt.setUser(user);
        importReceipt.setSupplier(supplier);
        importReceipt.setImportDate(request.getImportDate() != null ? request.getImportDate() : Instant.now());
        importReceipt.setNote(request.getNote());
        importReceipt.setCreatedAt(Instant.now());
        importReceipt = importReceiptRepository.save(importReceipt);

        for (ImportReceiptItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            if (itemRequest.getQuantity() <= 0) {
                throw new AppException(ErrorCode.INVALID_IMPORT_QUANTITY);
            }

            ImportReceiptItem item = new ImportReceiptItem();
            item.setImportReceipt(importReceipt);
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setRemainingQuantity(itemRequest.getQuantity());
            item.setImportPrice(itemRequest.getImportPrice());
            item.setExpireDate(itemRequest.getExpireDate());
            item.setNote(itemRequest.getNote());

            importReceiptItemRepository.save(item);
        }

        importReceipt = importReceiptRepository.findByIdWithRelations(importReceipt.getId());
        ImportReceiptResponse response = importReceiptMapper.toResponse(importReceipt);
        List<ImportReceiptItem> items = importReceiptItemRepository.findByImportReceiptId(importReceipt.getId());
        response.setItems(importReceiptMapper.toItemResponseList(items));

        return response;
    }

    @Override
    public ImportReceiptResponse getById(Integer id) {
        ImportReceipt importReceipt = importReceiptRepository.findByIdWithRelations(id);
        if (importReceipt == null) {
            throw new AppException(ErrorCode.IMPORT_RECEIPT_NOT_FOUND);
        }

        ImportReceiptResponse response = importReceiptMapper.toResponse(importReceipt);
        List<ImportReceiptItem> items = importReceiptItemRepository.findByImportReceiptId(id);
        response.setItems(importReceiptMapper.toItemResponseList(items));

        return response;
    }

    @Override
    public List<ImportReceiptResponse> getAll() {
        return importReceiptRepository.findAll().stream()
                .map(importReceipt -> {
                    ImportReceiptResponse response = importReceiptMapper.toResponse(importReceipt);
                    List<ImportReceiptItem> items = importReceiptItemRepository.findByImportReceiptId(importReceipt.getId());
                    response.setItems(importReceiptMapper.toItemResponseList(items));
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public ImportReceiptResponse getByIdWithFilter(Integer id, Boolean hasRemaining) {
        ImportReceipt importReceipt = importReceiptRepository.findByIdWithRelations(id);
        if (importReceipt == null) {
            throw new AppException(ErrorCode.IMPORT_RECEIPT_NOT_FOUND);
        }

        ImportReceiptResponse response = importReceiptMapper.toResponse(importReceipt);
        List<ImportReceiptItem> items = importReceiptItemRepository.findByImportReceiptIdAndRemainingStatus(id, hasRemaining);
        response.setItems(importReceiptMapper.toItemResponseList(items));

        return response;
    }
}

