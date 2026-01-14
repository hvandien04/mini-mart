package com.example.mini_mart.repository;

import com.example.mini_mart.entity.ImportReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ImportReceiptItemRepository extends JpaRepository<ImportReceiptItem, Integer> {
    @Query("SELECT iri FROM ImportReceiptItem iri JOIN FETCH iri.product WHERE iri.product.id = :productId AND iri.remainingQuantity > 0 ORDER BY iri.expireDate ASC NULLS LAST")
    List<ImportReceiptItem> findAvailableItemsByProductIdOrderByExpireDate(@Param("productId") Integer productId);

    @Query("SELECT iri FROM ImportReceiptItem iri JOIN FETCH iri.product WHERE iri.remainingQuantity > 0 AND iri.expireDate IS NOT NULL AND iri.expireDate <= :date")
    List<ImportReceiptItem> findExpiringItems(@Param("date") LocalDate date);

    @Query("SELECT iri FROM ImportReceiptItem iri JOIN FETCH iri.product WHERE iri.importReceipt.id = :importReceiptId")
    List<ImportReceiptItem> findByImportReceiptId(@Param("importReceiptId") Integer importReceiptId);
    
    /**
     * Tìm items theo import receipt ID và filter theo trạng thái (remainingQuantity)
     * @param importReceiptId ID của import receipt
     * @param hasRemaining true nếu còn hàng (remainingQuantity > 0), false nếu hết hàng (remainingQuantity = 0), null = tất cả
     * @return Danh sách items
     */
    @Query("SELECT iri FROM ImportReceiptItem iri JOIN FETCH iri.product WHERE iri.importReceipt.id = :importReceiptId " +
           "AND (:hasRemaining IS NULL OR " +
           "     (:hasRemaining = true AND iri.remainingQuantity > 0) OR " +
           "     (:hasRemaining = false AND iri.remainingQuantity = 0))")
    List<ImportReceiptItem> findByImportReceiptIdAndRemainingStatus(
        @Param("importReceiptId") Integer importReceiptId,
        @Param("hasRemaining") Boolean hasRemaining
    );
}

