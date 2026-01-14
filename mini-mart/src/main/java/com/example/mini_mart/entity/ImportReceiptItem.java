package com.example.mini_mart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "import_receipt_item")
public class ImportReceiptItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "import_receipt_id", nullable = false)
    private ImportReceipt importReceipt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "remaining_quantity", nullable = false)
    private Integer remainingQuantity;

    @NotNull
    @Column(name = "import_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal importPrice;

    @Column(name = "expire_date")
    private LocalDate expireDate;

    @Size(max = 1000)
    @Column(name = "note", length = 1000)
    private String note;

    @OneToMany(mappedBy = "importReceiptItem")
    private List<ExportReceiptItem> exportReceiptItems = new ArrayList<>();

}