-- Migration: Thêm ràng buộc Product - Supplier
-- Nghiệp vụ: Mỗi sản phẩm phải thuộc về một nhà cung cấp
-- Khi tạo phiếu nhập kho, chỉ được nhập các sản phẩm thuộc nhà cung cấp đã chọn

-- Thêm cột supplier_id vào bảng product (cho phép NULL tạm thời)
-- V4 sẽ insert products với supplier_id, sau đó V5 sẽ set NOT NULL và thêm FK
ALTER TABLE product
ADD COLUMN supplier_id INT NULL AFTER category_id;
