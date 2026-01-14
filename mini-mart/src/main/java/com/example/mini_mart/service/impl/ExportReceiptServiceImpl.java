package com.example.mini_mart.service.impl;

import com.example.mini_mart.dto.request.ExportReceiptCreateRequest;
import com.example.mini_mart.dto.request.ExportReceiptItemRequest;
import com.example.mini_mart.dto.response.ExportReceiptResponse;
import com.example.mini_mart.entity.*;
import com.example.mini_mart.exception.AppException;
import com.example.mini_mart.exception.ErrorCode;
import com.example.mini_mart.mapper.ExportReceiptMapper;
import com.example.mini_mart.repository.*;
import com.example.mini_mart.service.ExportReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportReceiptServiceImpl implements ExportReceiptService {

    private final ExportReceiptRepository exportReceiptRepository;
    private final ExportReceiptItemRepository exportReceiptItemRepository;
    private final ImportReceiptItemRepository importReceiptItemRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ExportReceiptMapper exportReceiptMapper;

    @Override
    @Transactional
    public ExportReceiptResponse create(ExportReceiptCreateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        ExportReceipt exportReceipt = new ExportReceipt();
        exportReceipt.setUser(user);
        exportReceipt.setCustomer(customer);
        exportReceipt.setExportDate(request.getExportDate() != null ? request.getExportDate() : Instant.now());
        exportReceipt.setNote(request.getNote());
        exportReceipt.setCreatedAt(Instant.now());
        exportReceipt = exportReceiptRepository.save(exportReceipt);

        for (ExportReceiptItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            if (itemRequest.getQuantity() <= 0) {
                throw new AppException(ErrorCode.INVALID_EXPORT_QUANTITY);
            }

            List<ImportReceiptItem> availableBatches = importReceiptItemRepository
                    .findAvailableItemsByProductIdOrderByExpireDate(itemRequest.getProductId());

            int totalStock = availableBatches.stream()
                    .mapToInt(ImportReceiptItem::getRemainingQuantity)
                    .sum();

            if (totalStock < itemRequest.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            int remainingQuantity = itemRequest.getQuantity();
            for (ImportReceiptItem batch : availableBatches) {
                if (remainingQuantity <= 0) {
                    break;
                }

                int quantityToExport = Math.min(remainingQuantity, batch.getRemainingQuantity());

                ExportReceiptItem exportItem = new ExportReceiptItem();
                exportItem.setExportReceipt(exportReceipt);
                exportItem.setImportReceiptItem(batch);
                exportItem.setProduct(product);
                exportItem.setQuantity(quantityToExport);
                exportItem.setSellingPrice(itemRequest.getSellingPrice());
                exportItem.setNote(itemRequest.getNote());
                exportReceiptItemRepository.save(exportItem);

                batch.setRemainingQuantity(batch.getRemainingQuantity() - quantityToExport);
                importReceiptItemRepository.save(batch);

                remainingQuantity -= quantityToExport;
            }
        }

        exportReceipt = exportReceiptRepository.findByIdWithRelations(exportReceipt.getId());
        ExportReceiptResponse response = exportReceiptMapper.toResponse(exportReceipt);
        List<ExportReceiptItem> items = exportReceiptItemRepository.findByExportReceiptId(exportReceipt.getId());
        response.setItems(exportReceiptMapper.toItemResponseList(items));

        return response;
    }

    @Override
    public ExportReceiptResponse getById(Integer id) {
        ExportReceipt exportReceipt = exportReceiptRepository.findByIdWithRelations(id);
        if (exportReceipt == null) {
            throw new AppException(ErrorCode.EXPORT_RECEIPT_NOT_FOUND);
        }

        ExportReceiptResponse response = exportReceiptMapper.toResponse(exportReceipt);
        List<ExportReceiptItem> items = exportReceiptItemRepository.findByExportReceiptId(id);
        response.setItems(exportReceiptMapper.toItemResponseList(items));

        return response;
    }

    @Override
    public List<ExportReceiptResponse> getAll() {
        return exportReceiptRepository.findAll().stream()
                .map(exportReceipt -> {
                    ExportReceiptResponse response = exportReceiptMapper.toResponse(exportReceipt);
                    List<ExportReceiptItem> items = exportReceiptItemRepository.findByExportReceiptId(exportReceipt.getId());
                    response.setItems(exportReceiptMapper.toItemResponseList(items));
                    return response;
                })
                .collect(Collectors.toList());
    }
}

