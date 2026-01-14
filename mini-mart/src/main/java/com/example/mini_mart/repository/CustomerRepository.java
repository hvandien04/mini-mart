package com.example.mini_mart.repository;

import com.example.mini_mart.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    /**
     * Tìm khách hàng theo tên hoặc số điện thoại (case-insensitive, contains)
     * Dùng cho AI Assistant để tìm customer từ user message
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Customer> findByFullNameOrPhoneContainingIgnoreCase(@Param("keyword") String keyword);
}

