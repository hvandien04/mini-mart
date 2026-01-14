package com.example.mini_mart.service;

/**
 * Service để sync entities (Product, Customer) vào Qdrant với embeddings
 * Chạy khi:
 * - Application startup (nếu cần)
 * - Khi có entity mới/cập nhật
 */
public interface VectorSyncService {
    /**
     * Sync tất cả products vào Qdrant
     */
    void syncAllProducts();
    
    /**
     * Sync một product vào Qdrant
     * @param productId Product ID
     */
    void syncProduct(Integer productId);
    
    /**
     * Sync tất cả customers vào Qdrant
     */
    void syncAllCustomers();
    
    /**
     * Sync một customer vào Qdrant
     * @param customerId Customer ID
     */
    void syncCustomer(Integer customerId);
    
    /**
     * Xóa product khỏi Qdrant
     * @param productId Product ID
     */
    void deleteProduct(Integer productId);
    
    /**
     * Xóa customer khỏi Qdrant
     * @param customerId Customer ID
     */
    void deleteCustomer(Integer customerId);
    
    /**
     * Kiểm tra xem collection đã có data chưa
     * @return true nếu đã có data, false nếu chưa có
     */
    boolean isCollectionEmpty(String collectionName);
}

