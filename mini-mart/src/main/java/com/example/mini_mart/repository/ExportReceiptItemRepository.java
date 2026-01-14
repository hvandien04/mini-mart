package com.example.mini_mart.repository;

import com.example.mini_mart.entity.ExportReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportReceiptItemRepository extends JpaRepository<ExportReceiptItem, Integer> {
    @Query("SELECT eri FROM ExportReceiptItem eri JOIN FETCH eri.product JOIN FETCH eri.importReceiptItem WHERE eri.exportReceipt.id = :exportReceiptId")
    List<ExportReceiptItem> findByExportReceiptId(@Param("exportReceiptId") Integer exportReceiptId);
}

