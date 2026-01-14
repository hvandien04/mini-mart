package com.example.mini_mart.service.impl;

import com.example.mini_mart.entity.Customer;
import com.example.mini_mart.entity.Product;
import com.example.mini_mart.repository.CustomerRepository;
import com.example.mini_mart.repository.ProductRepository;
import com.example.mini_mart.service.GeminiService;
import com.example.mini_mart.service.QdrantService;
import com.example.mini_mart.service.VectorSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation của VectorSyncService
 * Sync products và customers vào Qdrant với embeddings
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VectorSyncServiceImpl implements VectorSyncService {
    
    private static final String PRODUCTS_COLLECTION = "products";
    private static final String CUSTOMERS_COLLECTION = "customers";
    private static final int EMBEDDING_DIMENSION = 768; // Gemini embedding-001 dimension
    
    private final QdrantService qdrantService;
    private final GeminiService geminiService;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    
    @Override
    public boolean isCollectionEmpty(String collectionName) {
        long count = qdrantService.getCollectionCount(collectionName);
        return count <= 0;
    }
    
    @Override
    public void syncAllProducts() {
        List<Float> testEmbedding = geminiService.generateEmbedding("test");
        if (testEmbedding.isEmpty()) {
            log.warn("Gemini API key not configured. Skipping vector sync to Qdrant. " +
                    "Please set GEMINI_API_KEY environment variable to enable vector search.");
            return;
        }
        
        log.info("Starting sync all products to Qdrant...");
        log.warn("WARNING: This will consume Gemini API quota. " +
                "Free tier limit: ~1500 embedding requests/day. " +
                "If you have many products, consider syncing in batches.");
        
        qdrantService.createCollectionIfNotExists(PRODUCTS_COLLECTION, EMBEDDING_DIMENSION);
        
        List<Product> products = productRepository.findAll();
        int successCount = 0;
        int failCount = 0;
        
        for (Product product : products) {
            try {
                syncProduct(product.getId());
                successCount++;
            } catch (Exception e) {
                failCount++;
                if (e.getMessage() != null && e.getMessage().contains("quota")) {
                    log.error("QUOTA EXCEEDED during sync. Stopping sync to prevent more quota usage.");
                    log.error("Synced {}/{} products before quota limit reached.", successCount, products.size());
                    break;
                }
            }
        }
        
        log.info("Completed sync {}/{} products to Qdrant ({} failed)", successCount, products.size(), failCount);
    }
    
    @Override
    public void syncProduct(Integer productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            log.warn("Product {} not found, skipping sync", productId);
            return;
        }
        
        List<Float> testEmbedding = geminiService.generateEmbedding("test");
        if (testEmbedding.isEmpty()) {
            log.debug("Gemini API key not configured, skipping sync for product {}", productId);
            return;
        }
        
        qdrantService.createCollectionIfNotExists(PRODUCTS_COLLECTION, EMBEDDING_DIMENSION);
        
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append(product.getName());
        if (product.getSku() != null) {
            textBuilder.append(" ").append(product.getSku());
        }
        if (product.getDescription() != null) {
            textBuilder.append(" ").append(product.getDescription());
        }
        if (product.getBrand() != null) {
            textBuilder.append(" ").append(product.getBrand());
        }
        
        String text = textBuilder.toString();
        
        List<Float> embedding = geminiService.generateEmbedding(text);
        if (embedding.isEmpty()) {
            log.warn("Failed to generate embedding for product {}", productId);
            return;
        }
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("productId", product.getId());
        metadata.put("name", product.getName());
        metadata.put("sku", product.getSku());
        metadata.put("type", "product");
        
        qdrantService.upsertPoint(PRODUCTS_COLLECTION, productId, embedding, metadata);
        
        log.debug("Synced product {} to Qdrant", productId);
    }
    
    @Override
    public void syncAllCustomers() {
        List<Float> testEmbedding = geminiService.generateEmbedding("test");
        if (testEmbedding.isEmpty()) {
            log.warn("Gemini API key not configured. Skipping vector sync to Qdrant. " +
                    "Please set GEMINI_API_KEY environment variable to enable vector search.");
            return;
        }
        
        log.info("Starting sync all customers to Qdrant...");
        
        qdrantService.createCollectionIfNotExists(CUSTOMERS_COLLECTION, EMBEDDING_DIMENSION);
        
        List<Customer> customers = customerRepository.findAll();
        int successCount = 0;
        int failCount = 0;
        boolean apiKeyValid = true; // Flag để tránh test lại nhiều lần
        
        log.info("Syncing {} customers with rate limiting (100ms delay between requests)...", customers.size());
        
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            try {
                syncCustomerWithoutTest(customer.getId(), apiKeyValid);
                successCount++;
                
                if (i < customers.size() - 1) {
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                failCount++;
                String errorMsg = e.getMessage() != null ? e.getMessage() : "";
                
                if (errorMsg.contains("429") || errorMsg.contains("TooManyRequests")) {
                    log.error("Rate limit (429) hit during sync. Waiting 5 seconds before continuing...");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    i--;
                    continue;
                }
                
                // Kiểm tra quota
                if (errorMsg.contains("quota") || errorMsg.contains("QUOTA EXCEEDED")) {
                    log.error("QUOTA EXCEEDED during sync. Stopping sync to prevent more quota usage.");
                    log.error("Synced {}/{} customers before quota limit reached.", successCount, customers.size());
                    apiKeyValid = false;
                    break;
                }
                
                log.warn("Failed to sync customer {}: {}", customer.getId(), errorMsg);
            }
        }
        
        log.info("Completed sync {}/{} customers to Qdrant ({} failed)", successCount, customers.size(), failCount);
    }
    
    private void syncCustomerWithoutTest(Integer customerId, boolean apiKeyValid) {
        if (!apiKeyValid) {
            return;
        }
        
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            log.warn("Customer {} not found, skipping sync", customerId);
            return;
        }
        
        qdrantService.createCollectionIfNotExists(CUSTOMERS_COLLECTION, EMBEDDING_DIMENSION);
        
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append(customer.getFullName());
        if (customer.getPhone() != null) {
            textBuilder.append(" ").append(customer.getPhone());
        }
        if (customer.getEmail() != null) {
            textBuilder.append(" ").append(customer.getEmail());
        }
        
        String text = textBuilder.toString();
        
        List<Float> embedding = geminiService.generateEmbedding(text);
        if (embedding.isEmpty()) {
            log.warn("Failed to generate embedding for customer {}", customerId);
            return;
        }
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("customerId", customer.getId());
        metadata.put("customerName", customer.getFullName());
        metadata.put("customerPhone", customer.getPhone());
        
        qdrantService.upsertPoint(CUSTOMERS_COLLECTION, customerId, embedding, metadata);
        log.debug("Synced customer {} to Qdrant", customerId);
    }
    
    @Override
    public void syncCustomer(Integer customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            log.warn("Customer {} not found, skipping sync", customerId);
            return;
        }
        
        List<Float> testEmbedding = geminiService.generateEmbedding("test");
        if (testEmbedding.isEmpty()) {
            log.debug("Gemini API key not configured, skipping sync for customer {}", customerId);
            return;
        }
        
        qdrantService.createCollectionIfNotExists(CUSTOMERS_COLLECTION, EMBEDDING_DIMENSION);
        
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append(customer.getFullName());
        if (customer.getPhone() != null) {
            textBuilder.append(" ").append(customer.getPhone());
        }
        if (customer.getEmail() != null) {
            textBuilder.append(" ").append(customer.getEmail());
        }
        
        String text = textBuilder.toString();
        
        List<Float> embedding = geminiService.generateEmbedding(text);
        if (embedding.isEmpty()) {
            log.warn("Failed to generate embedding for customer {}", customerId);
            return;
        }
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("customerId", customer.getId());
        metadata.put("fullName", customer.getFullName());
        metadata.put("phone", customer.getPhone());
        metadata.put("type", "customer");
        
        qdrantService.upsertPoint(CUSTOMERS_COLLECTION, customerId, embedding, metadata);
        
        log.debug("Synced customer {} to Qdrant", customerId);
    }
    
    @Override
    public void deleteProduct(Integer productId) {
        qdrantService.deletePoint(PRODUCTS_COLLECTION, productId);
        log.debug("Deleted product {} from Qdrant", productId);
    }
    
    @Override
    public void deleteCustomer(Integer customerId) {
        qdrantService.deletePoint(CUSTOMERS_COLLECTION, customerId);
        log.debug("Deleted customer {} from Qdrant", customerId);
    }
}

