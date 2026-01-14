package com.example.mini_mart.service.impl;

import com.example.mini_mart.dto.request.ProductCreateRequest;
import com.example.mini_mart.dto.request.ProductUpdateRequest;
import com.example.mini_mart.dto.response.ProductResponse;
import com.example.mini_mart.entity.Category;
import com.example.mini_mart.entity.Product;
import com.example.mini_mart.exception.AppException;
import com.example.mini_mart.exception.ErrorCode;
import com.example.mini_mart.mapper.ProductMapper;
import com.example.mini_mart.repository.CategoryRepository;
import com.example.mini_mart.repository.ProductRepository;
import com.example.mini_mart.repository.SupplierRepository;
import com.example.mini_mart.service.ProductService;
import com.example.mini_mart.service.VectorSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductMapper productMapper;
    private final VectorSyncService vectorSyncService;

    @Override
    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new AppException(ErrorCode.PRODUCT_SKU_EXISTS);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        com.example.mini_mart.entity.Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setIsActive(true);
        product.setCreatedAt(Instant.now());
        product = productRepository.save(product);
        

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse update(Integer id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (request.getSku() != null && !request.getSku().equals(product.getSku())) {
            if (productRepository.existsBySku(request.getSku())) {
                throw new AppException(ErrorCode.PRODUCT_SKU_EXISTS);
            }
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }

        if (request.getSupplierId() != null) {
            com.example.mini_mart.entity.Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
            product.setSupplier(supplier);
        }

        productMapper.updateEntity(product, request);
        product = productRepository.save(product);
        

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(product);
        
        try {
            vectorSyncService.deleteProduct(id);
            log.debug("Deleted product {} from Qdrant", id);
        } catch (Exception e) {
            log.warn("Failed to delete product {} from Qdrant: {}", id, e.getMessage());
        }
    }

    @Override
    public ProductResponse getById(Integer id) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findAll().stream()
                .filter(Product::getIsActive)
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getByCategoryId(Integer categoryId) {
        return productRepository.findAll().stream()
                .filter(p -> p.getCategory().getId().equals(categoryId))
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getBySupplierId(Integer supplierId) {
        return productRepository.findBySupplierIdAndIsActiveTrue(supplierId).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }
}

