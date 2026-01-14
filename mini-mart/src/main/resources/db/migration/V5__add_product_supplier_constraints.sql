-- Migration: Thêm ràng buộc NOT NULL và Foreign Key cho supplier_id
-- Chạy sau V4__seed_products.sql để đảm bảo tất cả products đã có supplier_id

-- Set NOT NULL cho supplier_id (sau khi V4 đã insert products với supplier_id)
ALTER TABLE product
MODIFY COLUMN supplier_id INT NOT NULL;

-- Thêm foreign key constraint
ALTER TABLE product
ADD CONSTRAINT FK_product_supplier
    FOREIGN KEY (supplier_id)
    REFERENCES supplier(id);

