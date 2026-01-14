package com.example.mini_mart.repository;

import com.example.mini_mart.entity.ImportReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ImportReceiptRepository extends JpaRepository<ImportReceipt, Integer> {
    @Query("SELECT ir FROM ImportReceipt ir JOIN FETCH ir.supplier JOIN FETCH ir.user WHERE ir.id = :id")
    ImportReceipt findByIdWithRelations(@Param("id") Integer id);

    @Query("SELECT ir FROM ImportReceipt ir WHERE ir.importDate BETWEEN :startDate AND :endDate")
    List<ImportReceipt> findByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}

