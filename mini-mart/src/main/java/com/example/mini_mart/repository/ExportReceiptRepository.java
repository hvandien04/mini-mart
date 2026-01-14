package com.example.mini_mart.repository;

import com.example.mini_mart.entity.ExportReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ExportReceiptRepository extends JpaRepository<ExportReceipt, Integer> {
    @Query("SELECT er FROM ExportReceipt er JOIN FETCH er.customer JOIN FETCH er.user WHERE er.id = :id")
    ExportReceipt findByIdWithRelations(@Param("id") Integer id);

    @Query("SELECT er FROM ExportReceipt er WHERE er.exportDate BETWEEN :startDate AND :endDate")
    List<ExportReceipt> findByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}

