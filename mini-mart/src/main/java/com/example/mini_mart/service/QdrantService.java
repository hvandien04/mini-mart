package com.example.mini_mart.service;

import java.util.List;
import java.util.Map;

/**
 * Service để tương tác với Qdrant Vector DB
 * - Tạo collection
 * - Insert vectors với metadata
 * - Search vectors bằng similarity
 */
public interface QdrantService {
    /**
     * Tạo collection nếu chưa tồn tại
     * @param collectionName Tên collection
     * @param vectorSize Kích thước vector (embedding dimension)
     */
    void createCollectionIfNotExists(String collectionName, int vectorSize);
    
    /**
     * Insert vector với metadata
     * @param collectionName Tên collection
     * @param pointId ID của point (phải là unsigned integer hoặc UUID theo Qdrant spec)
     * @param vector Embedding vector
     * @param metadata Metadata (ví dụ: productId, name, type)
     */
    void upsertPoint(String collectionName, Object pointId, List<Float> vector, Map<String, Object> metadata);
    
    /**
     * Search vectors tương tự nhất
     * @param collectionName Tên collection
     * @param queryVector Query vector
     * @param limit Số lượng kết quả
     * @return List các point với score
     */
    List<Map<String, Object>> searchSimilar(String collectionName, List<Float> queryVector, int limit);
    
    /**
     * Xóa point theo ID
     * @param collectionName Tên collection
     * @param pointId ID của point (phải là unsigned integer hoặc UUID theo Qdrant spec)
     */
    void deletePoint(String collectionName, Object pointId);
    
    /**
     * Kiểm tra xem collection có points chưa
     * @param collectionName Tên collection
     * @return Số lượng points trong collection, -1 nếu collection không tồn tại
     */
    long getCollectionCount(String collectionName);
}

